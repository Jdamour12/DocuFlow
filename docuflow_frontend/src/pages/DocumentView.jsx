"use client"
import { Box, Typography, CircularProgress, Alert, Button } from "@mui/material"
import { ArrowBack as ArrowBackIcon } from "@mui/icons-material"
import { useParams, useNavigate } from "react-router-dom"
import { useQuery, useQueryClient } from "react-query"

import DocumentDetail from "../components/DocumentDetail"
import { documentService } from "../services/documentService"

const DocumentView = () => {
    const { id } = useParams()
    const navigate = useNavigate()
    const queryClient = useQueryClient()

    const {
        data: document,
        isLoading,
        error,
    } = useQuery(["document", id], () => documentService.getDocument(Number(id)), { enabled: !!id })

    const handleDownloadDocument = async () => {
    if (!document) return

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

    const handleDocumentUpdate = () => {
        queryClient.invalidateQueries(["document", id])
        queryClient.invalidateQueries(["documents"])
    }  

return (
    <Box>
        <Box display="flex" alignItems="center" mb={3}>
        <Button startIcon={<ArrowBackIcon />} onClick={() => navigate(-1)} sx={{ mr: 2 }}>
            Back
        </Button>
        <Typography variant="h4">Document Details</Typography>
    </Box>

    {isLoading ? (
        <Box display="flex" justifyContent="center" my={4}>
            <CircularProgress />
        </Box>
    ) : error ? (
        <Alert severity="error">Failed to load document. Please try again later.</Alert>
    ) : document ? (
        <DocumentDetail document={document} onDownload={handleDownloadDocument} onUpdate={handleDocumentUpdate} />
    ) : (
        <Alert severity="error">Document not found</Alert>
    )}
    </Box>
    )
}

export default DocumentView