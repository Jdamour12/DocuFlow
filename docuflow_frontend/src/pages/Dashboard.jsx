"use client"

import { useState } from "react"
import {
    Typography,
    Box,
    Paper,
    Tabs,
    Tab,
    Button,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    CircularProgress,
    Alert,
} from "@mui/material"
import { Add as AddIcon } from "@mui/icons-material"
import { useNavigate } from "react-router-dom"
import { useQuery, useQueryClient } from "react-query"

import DocumentList from "../components/DocumentList"
import DocumentUpload from "../components/DocumentUpload"
import { documentService } from "../services/documentService"
import { DocumentStatus } from "../types/document"

const Dashboard = () => {
    const [tabValue, setTabValue] = useState(0)
    const [uploadDialogOpen, setUploadDialogOpen] = useState(false)
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
    const [documentToDelete, setDocumentToDelete] = useState(null)
    const [deleteError, setDeleteError] = useState(null)
    const [deleteLoading, setDeleteLoading] = useState(false)

    const navigate = useNavigate()
    const queryClient = useQueryClient()

    const {
        data: documents = [],
        isLoading,
        error,
    } = useQuery(["documents", tabValue], () => {
        switch (tabValue) {
          case 0: // All Documents
            return documentService.getAllDocuments()
          case 1: // Draft
            return documentService.getDocumentsByStatus(DocumentStatus.DRAFT)
          case 2: // Submitted
            return documentService.getDocumentsByStatus(DocumentStatus.SUBMITTED)
          case 3: // Under Review
            return documentService.getDocumentsByStatus(DocumentStatus.UNDER_REVIEW)
          case 4: // Approved
            return documentService.getDocumentsByStatus(DocumentStatus.APPROVED)
          case 5: // Rejected
            return documentService.getDocumentsByStatus(DocumentStatus.REJECTED)
        default:
            return documentService.getAllDocuments()
        }
    })

    const handleTabChange = (event, newValue) => {
        setTabValue(newValue)
    }

    const handleViewDocument = (document) => {
        navigate(`/documents/${document.id}`)
    }

    const handleEditDocument = (document) => {
        navigate(`/documents/${document.id}?edit=true`)
    }

    const handleDeleteDocument = (document) => {
        setDocumentToDelete(document)
        setDeleteDialogOpen(true)
    }

    const confirmDelete = async () => {
        if (!documentToDelete) return
    
        setDeleteLoading(true)
        setDeleteError(null)
    
        try {
            await documentService.deleteDocument(documentToDelete.id)
            queryClient.invalidateQueries(["documents"])
            setDeleteDialogOpen(false)
        }   catch (err) {
            setDeleteError(err.message || "Failed to delete document")
        }   finally {
            setDeleteLoading(false)
        }
    }

    const handleDownloadDocument = async (document) => {
        try {
        const blob = await documentService.getDocumentContent(document.id)
        const url = window.URL.createObjectURL(blob)
        const a = document.createElement("a")
        a.href = url
        a.download = document.fileName
        document.body.appendChild(a)
        a.click()
        window.URL.revokeObjectURL(url)
        document.body.removeChild(a)
        } catch (err) {
        console.error("Failed to download document", err)
        }
    }

    const handleUploadSuccess = () => {
        setUploadDialogOpen(false)
        queryClient.invalidateQueries(["documents"])
    }

return (
    <Box>
        <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4">Document Management</Typography>
        <Button variant="contained" startIcon={<AddIcon />} onClick={() => setUploadDialogOpen(true)}>
            Upload Document
        </Button>
        </Box>

        <Paper sx={{ mb: 3 }}>
        <Tabs
            value={tabValue}
            onChange={handleTabChange}
            indicatorColor="primary"
            textColor="primary"
            variant="scrollable"
            scrollButtons="auto"
        >  
            <Tab label="All Documents" />
            <Tab label="Draft" />
            <Tab label="Submitted" />
            <Tab label="Under Review" />
            <Tab label="Approved" />
            <Tab label="Rejected" />
        </Tabs>
        </Paper>

    {isLoading ? (
        <Box display="flex" justifyContent="center" my={4}>
            <CircularProgress />
        </Box>
    ) : error ? (
        <Alert severity="error">Failed to load documents. Please try again later.</Alert>
    ) : documents.length === 0 ? (
        <Paper sx={{ p: 4, textAlign: "center" }}>
            <Typography variant="h6" color="text.secondary">
                No documents found
            </Typography>
            <Button variant="outlined" startIcon={<AddIcon />} onClick={() => setUploadDialogOpen(true)} sx={{ mt: 2 }}>
            Upload your first document
        </Button>
        </Paper>
    ) : (
        <DocumentList
            documents={documents}
            onView={handleViewDocument}
            onEdit={handleEditDocument}
            onDelete={handleDeleteDocument}
            onDownload={handleDownloadDocument}
        />
    )}

      {/* Upload Dialog */}
    <Dialog open={uploadDialogOpen} onClose={() => setUploadDialogOpen(false)} maxWidth="md" fullWidth>
        <DialogTitle>Upload New Document</DialogTitle>
        <DialogContent>
            <DocumentUpload onUploadSuccess={handleUploadSuccess} />
        </DialogContent>
        <DialogActions>
            <Button onClick={() => setUploadDialogOpen(false)}>Cancel</Button>
        </DialogActions>
    </Dialog>

      {/* Delete Confirmation Dialog */}
        <Dialog open={deleteDialogOpen} onClose={() => setDeleteDialogOpen(false)}>
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>
            {deleteError && (
            <Alert severity="error" sx={{ mb: 2 }}>
                {deleteError}
            </Alert>
        )}
        <Typography>
            Are you sure you want to delete the document "{documentToDelete?.title}"? This action cannot be undone.
        </Typography>
        </DialogContent>
        <DialogActions>
            <Button onClick={() => setDeleteDialogOpen(false)} disabled={deleteLoading}>
            Cancel
            </Button>
            <Button
            onClick={confirmDelete}
            color="error"
            disabled={deleteLoading}
            startIcon={deleteLoading ? <CircularProgress size={20} /> : undefined}
        >
            {deleteLoading ? "Deleting..." : "Delete"}
        </Button>
        </DialogActions>
    </Dialog>
    </Box>
)
}

export default Dashboard

