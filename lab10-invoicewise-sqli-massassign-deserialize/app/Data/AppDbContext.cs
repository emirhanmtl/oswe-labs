using InvoiceWise.Models;
using Microsoft.EntityFrameworkCore;

namespace InvoiceWise.Data;

public class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }

    public DbSet<User> Users => Set<User>();
    public DbSet<Client> Clients => Set<Client>();
    public DbSet<Invoice> Invoices => Set<Invoice>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<User>(e =>
        {
            e.ToTable("users");
            e.Property(u => u.Id).HasColumnName("id");
            e.Property(u => u.Username).HasColumnName("username");
            e.Property(u => u.PasswordHash).HasColumnName("password_hash");
            e.Property(u => u.FullName).HasColumnName("full_name");
            e.Property(u => u.Email).HasColumnName("email");
            e.Property(u => u.IsAdmin).HasColumnName("is_admin");
            // Npgsql 6+ defaults DateTime -> "timestamp with time zone"; the actual
            // column (see db/schema.sql) is plain "timestamp", so pin the store type
            // explicitly instead of fighting Kind=Unspecified/Utc mismatches.
            e.Property(u => u.CreatedAt).HasColumnName("created_at").HasColumnType("timestamp without time zone");
        });

        modelBuilder.Entity<Client>(e =>
        {
            e.ToTable("clients");
            e.Property(c => c.Id).HasColumnName("id");
            e.Property(c => c.OwnerUserId).HasColumnName("owner_user_id");
            e.Property(c => c.Name).HasColumnName("name");
            e.Property(c => c.Email).HasColumnName("email");
            e.Property(c => c.Phone).HasColumnName("phone");
        });

        modelBuilder.Entity<Invoice>(e =>
        {
            e.ToTable("invoices");
            e.Property(i => i.Id).HasColumnName("id");
            e.Property(i => i.ClientId).HasColumnName("client_id");
            e.Property(i => i.OwnerUserId).HasColumnName("owner_user_id");
            e.Property(i => i.Amount).HasColumnName("amount");
            e.Property(i => i.Description).HasColumnName("description");
            e.Property(i => i.Status).HasColumnName("status");
            e.Property(i => i.CreatedAt).HasColumnName("created_at").HasColumnType("timestamp without time zone");
        });
    }
}
