"use client"

import { useState } from "react"
import {
    Box,
    Typography,
    Paper,
    Divider,
    Chip,
    Button,
    TextField,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    Grid,
    CircularProgress,
    Alert,
} from "@mui/material"
import { GetApp as DownloadIcon, Edit as EditIcon, Save as SaveIcon, Cancel as CancelIcon } from "@mui/icons-material"
import { format } from "date-fns"
import { DocumentStatus } from "../types/document"
import { documentService } from "../services/documentService"

const DocumentDetail = ({ document, onDownload, onUpdate }) => {
    const [editing, setEditing] = useState(false)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState(null)
    const [title, setTitle] = useState(document.title)
    const [description, setDescription] = useState(document.description || "")
    const [status, setStatus] = useState(document.status)
    const [comments, setComments] = useState("")

    const formatDate = (dateString) => {
        return format(new Date(dateString), "MMM dd, yyyy HH:mm")
    }

    const formatFileSize = (bytes) => {
        if (bytes === 0) return "0 Bytes"
        const k = 1024
        const sizes = ["Bytes", "KB", "MB", "GB", "TB"]
        const i = Math.floor(Math.log(bytes) / Math.log(k))
        return Number.parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + " " + sizes[i]
    }

    const getStatusColor = (status) => {
        switch (status) {
            case DocumentStatus.DRAFT:
                return "default"
            case DocumentStatus.SUBMITTED:
                return "info"
            case DocumentStatus.UNDER_REVIEW:
                return "warning"
            case DocumentStatus.APPROVED:
                return "success"
            case DocumentStatus.REJECTED:
                return "error"
            default:
            return "default"
        }
    }

    const handleSave = async () => {
        setLoading(true)
        setError(null)
    
        try {
          // Update metadata if title or description changed
            if (title !== document.title || description !== document.description) {
        await documentService.updateDocumentMetadata(document.id, title, description || undefined)
    }

      // Update status if changed
    if (status !== document.status) {
        await documentService.updateDocumentStatus(document.id, status, comments || undefined)
    }

    setEditing(false)
    onUpdate()
    } catch (err) {
        setError(err.message || "Failed to update document")
    } finally {
        setLoading(false)
    }
}

return (
    <Paper>
        <Box p={3}>
        {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
            {error}
            </Alert>
        )}

        <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
        <Typography variant="h5">
            {editing ? (
            <TextField
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                label="Title"
                variant="outlined"
                size="small"
                fullWidth
            />
            ) : (
                document.title
            )}
        </Typography>

        <Box>
            {editing ? (
                <>
                <Button
                    variant="contained"
                    color="primary"
                    startIcon={loading ? <CircularProgress size={20} /> : <SaveIcon />}
                    onClick={handleSave}
                    disabled={loading}
                    sx={{ mr: 1 }}
                >
                Save
                </Button>
                <Button
                    variant="outlined"
                    startIcon={<CancelIcon />}
                    onClick={() => {
                        setEditing(false)
                        setTitle(document.title)
                        setDescription(document.description || "")
                        setStatus(document.status)
                        setComments("")
                    }}
                    disabled={loading}
                >
                Cancel
                </Button>
            </>
            ) : (
                <>
                    <Button
                        variant="outlined"
                        startIcon={<DownloadIcon />}
                        onClick={() => onDownload(document)}
                        sx={{ mr: 1 }}
                    >  
                        Download
                    </Button>
                    <Button variant="outlined" startIcon={<EditIcon />} onClick={() => setEditing(true)}>
                    Edit
                </Button>
                </>
            )}
            </Box>
        </Box>

        <Divider sx={{ my: 2 }} />

        <Grid container spacing={2}>
            <Grid item xs={12} md={6}>
                <Typography variant="subtitle1" fontWeight="bold">
                    Document Information
                </Typography>
    
                <Box mt={2}>
                    <Typography variant="body2" color="text.secondary">
                    File Name
                    </Typography>
                    <Typography variant="body1">{document.fileName}</Typography>
            </Box>

            <Box mt={2}>
                <Typography variant="body2" color="text.secondary">
                    File Type
                </Typography>
                <Typography variant="body1">{document.contentType}</Typography>
            </Box>

            <Box mt={2}>
                <Typography variant="body2" color="text.secondary">
                    File Size
                </Typography>
                <Typography variant="body1">{formatFileSize(document.fileSize)}</Typography>
            </Box>

            <Box mt={2}>
                <Typography variant="body2" color="text.secondary">
                    Status
                </Typography>
                {editing ? (
                    <FormControl fullWidth margin="dense" size="small">  
                        <InputLabel>Status</InputLabel>  
                        <Select value={status} label="Status" onChange={(e) => setStatus(e.target.value)}>
                    {Object.values(DocumentStatus).map((status) => (  
                        <MenuItem key={status} value={status}>
                            {status}
                        </MenuItem>
                    ))}
                    </Select>
                </FormControl>
            ) : (
                <Chip label={document.status} color={getStatusColor(document.status)} size="small" sx={{ mt: 0.5 }} />
            )}
            </Box>

            {editing && status !== document.status && (
                <Box mt={2}>
                    <TextField
                        label="Status Change Comments"
                        multiline
                        rows={2}
                        value={comments}
                        onChange={(e) => setComments(e.target.value)}
                        fullWidth
                        size="small"
                    />
                </Box>
            )}
        </Grid>

        <Grid item xs={12} md={6}>
            <Typography variant="subtitle1" fontWeight="bold">
                Description
            </Typography>

            <Box mt={2}>
                {editing ? (
                    <TextField
                        multiline
                        rows={4}
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        fullWidth
                    />
                ) : (
                <Typography variant="body1">{document.description || "No description provided."}</Typography>
            )}
            </Box>

            <Box mt={3}>
                <Typography variant="subtitle1" fontWeight="bold">
                Audit Information
                </Typography>

                <Box mt={2}>
                    <Typography variant="body2" color="text.secondary">
                    Created By
                    </Typography>
                    <Typography variant="body1">{document.createdBy}</Typography>
                </Box>

                <Box mt={2}>
                    <Typography variant="body2" color="text.secondary">
                    Created At
                    </Typography>
                    <Typography variant="body1">{formatDate(document.createdAt)}</Typography>
                </Box>

                {document.lastModifiedBy && (
                <Box mt={2}>
                    <Typography variant="body2" color="text.secondary">
                    Last Modified By
                    </Typography>
                    <Typography variant="body1">{document.lastModifiedBy}</Typography>
                </Box>
                )}

                {document.lastModifiedAt && (
                <Box mt={2}>
                    <Typography variant="body2" color="text.secondary">
                    Last Modified At
                    </Typography>
                    <Typography variant="body1">{formatDate(document.lastModifiedAt)}</Typography>
                </Box>
                )}

                <Box mt={2}>
                    <Typography variant="body2" color="text.secondary">
                        Version
                    </Typography>
                    <Typography variant="body1">{document.version}</Typography>
                </Box>
            </Box>
        </Grid>
        </Grid>
    </Box>
    </Paper>
)
}

export default DocumentDetail