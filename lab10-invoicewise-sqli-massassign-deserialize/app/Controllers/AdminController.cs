using System.Runtime.Serialization.Formatters.Binary;
using InvoiceWise.Data;
using InvoiceWise.Filters;
using InvoiceWise.Models;
using InvoiceWise.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace InvoiceWise.Controllers;

[RequireAdmin]
public class AdminController : BaseController
{
    private const string DraftDir = "/app/drafts";

    public AdminController(AppDbContext db) : base(db) { }

    public async Task<IActionResult> Index()
    {
        var users = await Db.Users.OrderBy(u => u.Id).ToListAsync();
        return View(users);
    }

    public async Task<IActionResult> Drafts()
    {
        return View(await BuildViewModelAsync());
    }

    /// <summary>
    /// Generates a portable backup blob for one invoice's draft text. The blob is a
    /// base64-encoded BinaryFormatter serialization of a DraftCache - the same object
    /// type RestoreDraft below deserializes.
    /// </summary>
    [HttpPost]
    public async Task<IActionResult> ExportDraft(int invoiceId)
    {
        var invoice = await Db.Invoices.FindAsync(invoiceId);
        if (invoice == null) return NotFound();

        var path = Path.Combine(DraftDir, $"invoice_{invoiceId}.txt");
        var draft = new DraftCache(path, invoice.Description);

        // Written to the local drafts/ cache immediately...
        draft.Flush();

        // ...and also handed back as a portable backup blob, so the same draft can be
        // restored on another InvoiceWise instance via the restore tool below.
        using var ms = new MemoryStream();
        new BinaryFormatter().Serialize(ms, draft);

        var vm = await BuildViewModelAsync();
        vm.Blob = Convert.ToBase64String(ms.ToArray());
        vm.ExportedInvoiceId = invoiceId.ToString();
        return View("Drafts", vm);
    }

    /// <summary>
    /// Restores a previously exported draft backup blob. Restoring a draft just means
    /// re-materializing the DraftCache object ExportDraft handed out - no extra
    /// validation needed since it's our own backup format.
    /// </summary>
    [HttpPost]
    public async Task<IActionResult> RestoreDraft(string backup)
    {
        var vm = await BuildViewModelAsync();

        try
        {
            var bytes = Convert.FromBase64String(backup ?? "");
            using var ms = new MemoryStream(bytes);
            var formatter = new BinaryFormatter();
            var restored = formatter.Deserialize(ms);
            ViewBag.Success = $"Draft restored ({restored.GetType().Name}).";
        }
        catch (Exception)
        {
            ViewBag.Error = "Backup blob could not be restored.";
        }

        return View("Drafts", vm);
    }

    private async Task<DraftsViewModel> BuildViewModelAsync()
    {
        var invoices = await Db.Invoices.OrderBy(i => i.Id).ToListAsync();
        return new DraftsViewModel { Invoices = invoices };
    }
}
