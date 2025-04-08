"use client"

import { useState } from "react"
import { Button, TextField, Box, Typography, Paper, CircularProgress, Alert } from "@mui/material"
import { CloudUpload as CloudUploadIcon } from "@mui/icons-material"
import { useFormik } from "formik"
import * as yup from "yup"
import { documentService } from "../services/documentService"

const validationSchema = yup.object({
    title: yup.string().required("Title is required").max(100, "Title should be of maximum 100 characters length"),
    description: yup.string().max(1000, "Description should be of maximum 1000 characters length"),
    file: yup.mixed().required("File is required"),
})

const DocumentUpload = ({ onUploadSuccess }) => {
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState(null)
    const [success, setSuccess] = useState(false)

    const formik = useFormik({
        initialValues: {
            title: "",
            description: "",
            file: null,
        },  
        validationSchema: validationSchema,
        onSubmit: async (values) => {
            if (!values.file) return

            setLoading(true)
            setError(null)
            setSuccess(false)

            try {
            await documentService.uploadDocument(values.file, values.title, values.description || undefined)

        setSuccess(true)
        formik.resetForm()
        onUploadSuccess()
        } catch (err) {
            setError(err.message || "Failed to upload document")
        } finally {
            setLoading(false)
        }
    },
})

const handleFileChange = (event) => {
    if (event.target.files && event.target.files.length > 0) {
       formik.setFieldValue("file", event.target.files[0])
    }
}

return (
    <Paper>
        <Box p={3}>
          <Typography variant="h6" gutterBottom>
            Upload New Document
          </Typography>
  
          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}
  
          {success && (
            <Alert severity="success" sx={{ mb: 2 }}>
              Document uploaded successfully!
            </Alert>
          )}
  
          <form onSubmit={formik.handleSubmit}>
            <TextField
              fullWidth
              id="title"
              name="title"
              label="Title"
              value={formik.values.title}
              onChange={formik.handleChange}
              error={formik.touched.title && Boolean(formik.errors.title)}
            helperText={formik.touched.title && formik.errors.title}
            margin="normal"
          />

          <TextField
            fullWidth
            id="description"
            name="description"
            label="Description"
            multiline
            rows={4}
            value={formik.values.description}
            onChange={formik.handleChange}
            error={formik.touched.description && Boolean(formik.errors.description)}
                helperText={formik.touched.description && formik.errors.description}
                margin="normal"
            />

            <Box mt={2} mb={2}>
                <input accept="*/*" style={{ display: "none" }} id="file-upload" type="file" onChange={handleFileChange} />
                <label htmlFor="file-upload">
                    <Button variant="outlined" component="span" startIcon={<CloudUploadIcon />}>
                        Select File
                    </Button>
                </label>
                {formik.values.file && (
                    <Typography variant="body2" sx={{ mt: 1 }}>
                        Selected file: {formik.values.file.name}
                    </Typography>
                )}
                {formik.touched.file && formik.errors.file && (
                    <Typography color="error" variant="caption">
                        {formik.errors.file}
                    </Typography>
                )}
            </Box>

            <Box mt={3}>
                <Button
                    color="primary"
                    variant="contained"
                    type="submit"
                    disabled={loading}
                    startIcon={loading ? <CircularProgress size={20} /> : undefined}
                >  
                    {loading ? "Uploading..." : "Upload Document"}
                </Button>
            </Box>
        </form>
        </Box>
    </Paper>
)
}

export default DocumentUpload