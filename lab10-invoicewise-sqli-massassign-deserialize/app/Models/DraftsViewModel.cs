namespace InvoiceWise.Models;

public class DraftsViewModel
{
    public List<Invoice> Invoices { get; set; } = new();
    public string? Blob { get; set; }
    public string? ExportedInvoiceId { get; set; }
}
