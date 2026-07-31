namespace InvoiceWise.Models;

public class Client
{
    public int Id { get; set; }
    public int OwnerUserId { get; set; }
    public string Name { get; set; } = "";
    public string Email { get; set; } = "";
    public string Phone { get; set; } = "";
}
