namespace InvoiceWise.Models;

public class Invoice
{
    public int Id { get; set; }
    public int ClientId { get; set; }
    public int OwnerUserId { get; set; }
    public decimal Amount { get; set; }
    public string Description { get; set; } = "";
    public string Status { get; set; } = "unpaid";
    public DateTime CreatedAt { get; set; }
}
