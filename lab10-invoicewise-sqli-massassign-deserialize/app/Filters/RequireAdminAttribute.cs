using InvoiceWise.Data;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;
using Microsoft.EntityFrameworkCore;

namespace InvoiceWise.Filters;

public class RequireAdminAttribute : ActionFilterAttribute
{
    public override async Task OnActionExecutionAsync(ActionExecutingContext context, ActionExecutionDelegate next)
    {
        var userId = context.HttpContext.Session.GetInt32("UserId");
        if (userId == null)
        {
            context.Result = new RedirectToActionResult("Login", "Auth", null);
            return;
        }

        var db = context.HttpContext.RequestServices.GetRequiredService<AppDbContext>();

        // Always re-read straight from the database instead of trusting anything cached
        // in the session - a role change (e.g. a self-service promotion) must take effect
        // on the very next request, not just after a fresh login.
        var isAdmin = await db.Users
            .Where(u => u.Id == userId.Value)
            .Select(u => u.IsAdmin)
            .FirstOrDefaultAsync();

        if (!isAdmin)
        {
            context.Result = new StatusCodeResult(403);
            return;
        }

        await next();
    }
}
