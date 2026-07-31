namespace InvoiceWise.Models;

public class User
{
    public int Id { get; set; }
    public string Username { get; set; } = "";
    public string PasswordHash { get; set; } = "";
    public string FullName { get; set; } = "";
    public string Email { get; set; } = "";

    // Grants full admin access: user management plus the invoice-draft backup/restore
    // tools. Only ever meant to be set from the admin panel by an existing admin.
    public bool IsAdmin { get; set; }

    public DateTime CreatedAt { get; set; }
}
