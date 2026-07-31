using InvoiceWise.Data;
using Microsoft.EntityFrameworkCore;

// The invoice-draft cache (see Services/DraftCache.cs) writes to disk as part of its own
// (de)serialization round-trip, which .NET's SerializationGuard blocks by default since
// file I/O triggered mid-deserialization is exactly the shape of a real exploit primitive.
// This app already re-enables BinaryFormatter itself (see the csproj) for that same
// feature, so disable the guard too rather than leave the feature half-working.
AppContext.SetSwitch("Switch.System.Runtime.Serialization.SerializationGuard.AllowFileWrites", true);

var builder = WebApplication.CreateBuilder(args);

var connString = Environment.GetEnvironmentVariable("DB_CONNECTION")
    ?? "Host=db;Database=invoicewise;Username=invoicewise;Password=invoicewise_pw";

builder.Services.AddDbContext<AppDbContext>(options => options.UseNpgsql(connString));
builder.Services.AddSingleton(_ => new RawDb(connString));

builder.Services.AddDistributedMemoryCache();
builder.Services.AddSession(options =>
{
    options.IdleTimeout = TimeSpan.FromHours(4);
    options.Cookie.HttpOnly = true;
    options.Cookie.Name = "invoicewise.sid";
});

builder.Services.AddControllersWithViews()
    // Lets ops tweak page copy (banners, labels) by editing a .cshtml file directly on
    // the running container without a full image rebuild - views are re-read from disk
    // whenever their file content changes.
    .AddRazorRuntimeCompilation();

var app = builder.Build();

app.UseStaticFiles();
app.UseSession();
app.UseRouting();

app.MapControllerRoute(
    name: "default",
    pattern: "{controller=Invoices}/{action=Index}/{id?}");

// The schema/seed data is created by db/schema.sql via Postgres' init hook, so this is
// just a startup retry loop in case the app container wins the race against the DB.
using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
    for (var attempt = 0; attempt < 15; attempt++)
    {
        try
        {
            db.Database.OpenConnection();
            db.Database.CloseConnection();
            break;
        }
        catch
        {
            Thread.Sleep(2000);
        }
    }
}

app.Run();
