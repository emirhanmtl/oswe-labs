using InvoiceWise.Data;
using InvoiceWise.Filters;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace InvoiceWise.Controllers;

[RequireLogin]
public class AccountController : BaseController
{
    public AccountController(AppDbContext db) : base(db) { }

    public async Task<IActionResult> Profile()
    {
        var user = await GetCurrentUserAsync();
        if (user == null) return RedirectToAction("Login", "Auth");
        return View(user);
    }

    [HttpPost]
    public async Task<IActionResult> UpdateProfile()
    {
        var user = await GetCurrentUserAsync();
        if (user == null) return RedirectToAction("Login", "Auth");

        // Self-service profile editor: whatever fields the form posts get bound straight
        // onto the tracked entity, so adding a new self-service field later (phone,
        // timezone, ...) never needs a controller change.
        await TryUpdateModelAsync(user, string.Empty);

        Db.Entry(user).State = EntityState.Modified;
        await Db.SaveChangesAsync();

        ViewBag.Success = "Profile updated.";
        return View("Profile", user);
    }
}
