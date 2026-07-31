using InvoiceWise.Data;
using InvoiceWise.Filters;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace InvoiceWise.Controllers;

[RequireLogin]
public class InvoicesController : BaseController
{
    private readonly RawDb _rawDb;

    public InvoicesController(AppDbContext db, RawDb rawDb) : base(db) => _rawDb = rawDb;

    public async Task<IActionResult> Index()
    {
        var userId = CurrentUserId!.Value;
        var invoices = await Db.Invoices
            .Where(i => i.OwnerUserId == userId)
            .OrderByDescending(i => i.CreatedAt)
            .ToListAsync();

        return View(invoices);
    }

    [HttpGet]
    public async Task<IActionResult> Search(string? email)
    {
        ViewBag.Email = email;

        if (string.IsNullOrWhiteSpace(email))
        {
            return View(new List<RawDb.InvoiceHit>());
        }

        var results = await _rawDb.SearchInvoicesByClientEmailAsync(email);
        return View(results);
    }
}
