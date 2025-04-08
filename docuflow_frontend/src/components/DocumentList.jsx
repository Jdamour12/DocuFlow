"use client"

import { useState } from "react"
import {
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    IconButton,
    Chip,
    Tooltip,
    TablePagination,
} from "@mui/material"
import {
    Visibility as VisibilityIcon,
    Edit as EditIcon,
    Delete as DeleteIcon,
    GetApp as DownloadIcon,
} from "@mui/icons-material"
import { format } from "date-fns"
import { DocumentStatus } from "../types/document"

const DocumentList = ({ documents, onView, onEdit, onDelete, onDownload }) => {
    const [page, setPage] = useState(0)
    const [rowsPerPage, setRowsPerPage] = useState(10)

    const handleChangePage = (event, newPage) => {
        setPage(newPage)
    }

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(Number.parseInt(event.target.value, 10))
        setPage(0)
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

    return (
        <Paper>
        <TableContainer>
            <Table>
                <TableHead>
                    <TableRow>
                        <TableCell>Title</TableCell>
                        <TableCell>File Name</TableCell>
                        <TableCell>Size</TableCell>
                        <TableCell>Created By</TableCell>
                        <TableCell>Created At</TableCell>
                        <TableCell>Status</TableCell>
                        <TableCell align="right">Actions</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                {documents.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map((document) => (
                    <TableRow key={document.id}>
                        <TableCell>{document.title}</TableCell>
                        <TableCell>{document.fileName}</TableCell>
                        <TableCell>{formatFileSize(document.fileSize)}</TableCell>
                    <TableCell>{document.createdBy}</TableCell>
                    <TableCell>{formatDate(document.createdAt)}</TableCell>
                <TableCell>
                    <Chip label={document.status} color={getStatusColor(document.status)} size="small" />
                    </TableCell>
                    <TableCell align="right">
                    <Tooltip title="View">
                    <IconButton size="small" onClick={() => onView(document)}>
                        <VisibilityIcon fontSize="small" />
                    </IconButton>
                    </Tooltip>
                    <Tooltip title="Download">
                    <IconButton size="small" onClick={() => onDownload(document)}>
                        <DownloadIcon fontSize="small" />
                    </IconButton>
                    </Tooltip>
                    <Tooltip title="Edit">
                    <IconButton size="small" onClick={() => onEdit(document)}>
                        <EditIcon fontSize="small" />
                    </IconButton>
                    </Tooltip>
                    <Tooltip title="Delete">
                    <IconButton size="small" onClick={() => onDelete(document)}>
                        <DeleteIcon fontSize="small" />
                    </IconButton>
                    </Tooltip>
                </TableCell>
                </TableRow>
            ))}
            </TableBody>
            </Table>
        </TableContainer>
        <TablePagination
            rowsPerPageOptions={[5, 10, 25]}
            component="div"
            count={documents.length}
            rowsPerPage={rowsPerPage}
            page={page}
            onPageChange={handleChangePage}
            onRowsPerPageChange={handleChangeRowsPerPage}
        />
    </Paper>
)
}

export default DocumentList

