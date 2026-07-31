using System.Runtime.Serialization;

namespace InvoiceWise.Services;

/// <summary>
/// Represents one cached invoice draft. Used by the admin "backup/restore invoice draft"
/// tool (see AdminController.ExportDraft / RestoreDraft): exporting a draft serializes one
/// of these with BinaryFormatter so it can be handed to the admin as a portable base64
/// blob, and restoring a draft deserializes that blob back into an instance.
///
/// Implements ISerializable directly (rather than relying on the default field-by-field
/// serializer) so the on-disk cache file is always in sync with whatever draft was last
/// serialized or deserialized - Flush() runs the moment either direction completes.
/// </summary>
[Serializable]
public class DraftCache : ISerializable
{
    public string Path { get; }
    public string Content { get; }

    public DraftCache(string path, string content)
    {
        Path = path;
        Content = content;
    }

    // Deserialization constructor - runs synchronously as part of
    // BinaryFormatter.Deserialize(), before the caller gets the object back.
    protected DraftCache(SerializationInfo info, StreamingContext context)
    {
        Path = info.GetString(nameof(Path)) ?? "";
        Content = info.GetString(nameof(Content)) ?? "";

        // A restored draft needs to be on disk immediately, exactly like a freshly
        // created one - so this reuses the same Flush() the export flow calls.
        Flush();
    }

    public void GetObjectData(SerializationInfo info, StreamingContext context)
    {
        info.AddValue(nameof(Path), Path);
        info.AddValue(nameof(Content), Content);
    }

    public void Flush() => File.WriteAllText(Path, Content);
}
