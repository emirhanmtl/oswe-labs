using InvoiceWise.Data;
using InvoiceWise.Models;
using Microsoft.AspNetCore.Mvc;

namespace InvoiceWise.Controllers;

public abstract class BaseController : Controller
{
    protected readonly AppDbContext Db;

    protected BaseController(AppDbContext db) => Db = db;

    protected int? CurrentUserId => HttpContext.Session.GetInt32("UserId");

    protected async Task<User?> GetCurrentUserAsync()
    {
        var id = CurrentUserId;
        if (id == null) return null;
        return await Db.Users.FindAsync(id.Value);
    }
}
