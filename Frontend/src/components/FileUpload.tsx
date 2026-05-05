import { useState, useEffect, useRef, useCallback } from "react";
import { authFetch } from "../utils/authFetch";
import { useStompClient } from "../hooks/useStompClient";

interface UploadProgress {
  jobId: string;
  fileName: string;
  status: string;
  message: string;
  queuePosition: number;
}

interface FileJob {
  jobId: string;
  fileName: string;
  status: string;
  message: string;
}

export default function FileUpload() {
  const [uploading, setUploading] = useState(false);
  const [jobs, setJobs] = useState<FileJob[]>([]);
  const [error, setError] = useState("");
  const { connected, subscribe } = useStompClient();
  const subscribed = useRef(false);

  const handleProgress = useCallback((progress: UploadProgress) => {
    setJobs((prev) => {
      const idx = prev.findIndex((j) => j.jobId === progress.jobId);
      const updated: FileJob = {
        jobId: progress.jobId,
        fileName: progress.fileName,
        status: progress.status,
        message: progress.message,
      };
      if (idx >= 0) {
        const next = [...prev];
        next[idx] = updated;
        return next;
      }
      return [...prev, updated];
    });
  }, []);

  useEffect(() => {
    if (!connected || subscribed.current) return;
    subscribed.current = true;
    const unsub = subscribe<UploadProgress>("/user/queue/upload-progress", handleProgress);
    return () => {
      subscribed.current = false;
      unsub();
    };
  }, [connected, subscribe, handleProgress]);

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const form = e.currentTarget;
    const fileInput = form.elements.namedItem("file") as HTMLInputElement;
    const files = fileInput?.files;

    if (!files || files.length === 0) {
      setError("Please select one or more .tdf files.");
      return;
    }

    setUploading(true);
    setError("");
    setJobs([]);

    const formData = new FormData();
    for (let i = 0; i < files.length; i++) {
      formData.append("file", files[i]);
    }

    try {
      const res = await authFetch("/api/upload", { method: "POST", body: formData });

      if (res.ok) {
        const data = await res.json();
        // Seed jobs from accepted jobIds
        const seeded: FileJob[] = (data.jobIds as string[]).map((id: string, idx: number) => ({
          jobId: id,
          fileName: files[idx]?.name ?? `File ${idx + 1}`,
          status: "QUEUED",
          message: "Waiting…",
        }));
        setJobs(seeded);
        form.reset();
      } else {
        const text = await res.text();
        setError(text || "Upload failed");
      }
    } catch (err) {
      setError("Network error: " + (err instanceof Error ? err.message : String(err)));
    } finally {
      setUploading(false);
    }
  };

  return (
    <div className="file-upload">
      <h3>Import TDF Files</h3>
      <form onSubmit={handleSubmit}>
        <input type="file" name="file" accept=".tdf" multiple />
        <button type="submit" disabled={uploading}>
          {uploading ? "Uploading…" : "Upload"}
        </button>
      </form>

      {error && <p className="upload-message error">{error}</p>}

      {jobs.length > 0 && (
        <ul className="upload-jobs">
          {jobs.map((job) => (
            <li key={job.jobId} className={`upload-job ${job.status.toLowerCase()}`}>
              <span className="upload-job-name">{job.fileName}</span>
              <span className="upload-job-status">{job.status}</span>
              <span className="upload-job-message">{job.message}</span>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
