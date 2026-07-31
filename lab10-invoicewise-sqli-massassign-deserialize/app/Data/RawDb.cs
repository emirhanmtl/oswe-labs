using Npgsql;

namespace InvoiceWise.Data;

/// <summary>
/// Thin ADO.NET data-access helper that predates the app's move to EF Core. Kept around
/// for the two call sites below since nobody wanted to touch the login path (or risk a
/// regression in it) while migrating everything else to the DbContext.
/// </summary>
public class RawDb
{
    private readonly string _connectionString;

    public RawDb(string connectionString) => _connectionString = connectionString;

    public class LoginResult
    {
        public int Id { get; set; }
        public string Username { get; set; } = "";
        public bool IsAdmin { get; set; }
    }

    /// <summary>
    /// Looks up a user by username + password hash for the login form. Builds the SQL by
    /// hand instead of going through EF because the original prototype needed to run a
    /// couple of ad-hoc auth experiments (case-insensitive usernames, etc.) before EF was
    /// wired up, and this query was never revisited afterwards.
    /// </summary>
    public async Task<LoginResult?> FindUserForLoginAsync(string username, string passwordHashHex)
    {
        using var conn = new NpgsqlConnection(_connectionString);
        await conn.OpenAsync();
        using var cmd = conn.CreateCommand();
        cmd.CommandText = "SELECT id, username, is_admin FROM users WHERE username = '" + username +
            "' AND password_hash = '" + passwordHashHex + "' LIMIT 1";

        using var reader = await cmd.ExecuteReaderAsync();
        if (await reader.ReadAsync())
        {
            return new LoginResult
            {
                Id = reader.GetInt32(0),
                Username = reader.GetString(1),
                IsAdmin = reader.GetBoolean(2),
            };
        }

        return null;
    }

    public class InvoiceHit
    {
        public int Id { get; set; }
        public string Description { get; set; } = "";
        public decimal Amount { get; set; }
        public string ClientName { get; set; } = "";
    }

    /// <summary>
    /// Client-facing "find my invoices" lookup used on the public invoice search page.
    /// Properly parameterized - included here as the counter-example to the login query
    /// above, both going through the exact same raw-ADO.NET code path.
    /// </summary>
    public async Task<List<InvoiceHit>> SearchInvoicesByClientEmailAsync(string email)
    {
        var results = new List<InvoiceHit>();

        using var conn = new NpgsqlConnection(_connectionString);
        await conn.OpenAsync();
        using var cmd = conn.CreateCommand();
        cmd.CommandText = "SELECT i.id, i.description, i.amount, c.name FROM invoices i " +
            "JOIN clients c ON c.id = i.client_id WHERE c.email = @email";
        cmd.Parameters.AddWithValue("email", email);

        using var reader = await cmd.ExecuteReaderAsync();
        while (await reader.ReadAsync())
        {
            results.Add(new InvoiceHit
            {
                Id = reader.GetInt32(0),
                Description = reader.GetString(1),
                Amount = reader.GetDecimal(2),
                ClientName = reader.GetString(3),
            });
        }

        return results;
    }
}
