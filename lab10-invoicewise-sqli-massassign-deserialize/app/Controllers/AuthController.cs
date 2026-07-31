using System.Security.Cryptography;
using System.Text;
using InvoiceWise.Data;
using Microsoft.AspNetCore.Mvc;

namespace InvoiceWise.Controllers;

public class AuthController : Controller
{
    private readonly RawDb _rawDb;

    public AuthController(RawDb rawDb) => _rawDb = rawDb;

    [HttpGet]
    public IActionResult Login() => View();

    [HttpPost]
    public async Task<IActionResult> Login(string username, string password)
    {
        var hash = Sha256Hex(password ?? "");
        var user = await _rawDb.FindUserForLoginAsync(username ?? "", hash);

        if (user != null)
        {
            HttpContext.Session.SetInt32("UserId", user.Id);
            return RedirectToAction("Index", "Invoices");
        }

        ViewBag.Error = "Invalid username or password.";
        return View();
    }

    [HttpPost]
    public IActionResult Logout()
    {
        HttpContext.Session.Clear();
        return RedirectToAction("Login");
    }

    private static string Sha256Hex(string input)
    {
        var bytes = SHA256.HashData(Encoding.UTF8.GetBytes(input));
        return Convert.ToHexString(bytes).ToLowerInvariant();
    }
}
