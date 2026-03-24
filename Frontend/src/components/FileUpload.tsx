import { useState } from "react";

export default function FileUpload() {
  const [status, setStatus] = useState<"idle" | "uploading" | "success" | "error">("idle");
  const [message, setMessage] = useState("");

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const form = e.currentTarget;
    const fileInput = form.elements.namedItem("file") as HTMLInputElement;
    const file = fileInput?.files?.[0];

    if (!file) {
      setStatus("error");
      setMessage("Please select a .tdf file.");
      return;
    }

    setStatus("uploading");
    setMessage("");

    const formData = new FormData();
    formData.append("file", file);

    try {
      const res = await fetch("/api/upload", { method: "POST", body: formData });
      const text = await res.text();

      if (res.ok) {
        setStatus("success");
        setMessage(text);
        form.reset();
      } else {
        setStatus("error");
        setMessage(text || "Upload failed");
      }
    } catch (err) {
      setStatus("error");
      setMessage("Network error: " + (err instanceof Error ? err.message : String(err)));
    }
  };

  return (
    <div className="file-upload">
      <h3>Import TDF File</h3>
      <form onSubmit={handleSubmit}>
        <input type="file" name="file" accept=".tdf" />
        <button type="submit" disabled={status === "uploading"}>
          {status === "uploading" ? "Uploading…" : "Upload"}
        </button>
      </form>
      {message && (
        <p className={`upload-message ${status}`}>{message}</p>
      )}
    </div>
  );
}
