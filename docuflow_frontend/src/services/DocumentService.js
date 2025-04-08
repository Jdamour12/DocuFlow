import api from "./api"

const DOCUMENTS_URL = "/documents"

export const documentService = {
  // Get all documents
    getAllDocuments: async () => {
        const response = await api.get(DOCUMENTS_URL)
        return response.data
    },

    // Get document by ID
    getDocument: async (id) => {
        const response = await api.get(`${DOCUMENTS_URL}/${id}`)
        return response.data
    },

    // Get document content (file download)
    getDocumentContent: async (id) => {
        const response = await api.get(`${DOCUMENTS_URL}/${id}/content`, {
            responseType: "blob",
        })
        return response.data
    },

    // Get documents by user
    getDocumentsByUser: async (username) => {
        const response = await api.get(`${DOCUMENTS_URL}/user/${username}`)
        return response.data
    },  

    // Get documents by status
    getDocumentsByStatus: async (status) => {
        const response = await api.get(`${DOCUMENTS_URL}/status/${status}`)
        return response.data
    },  

    // Search documents
    searchDocuments: async (query) => {
        const response = await api.get(`${DOCUMENTS_URL}/search`, {
            params: { query },
        })
        return response.data
    },

    // Upload document
    uploadDocument: async (file, title, description) => {
        const formData = new FormData()
        formData.append("file", file)
        formData.append("title", title)
    
        if (description) {
            formData.append("description", description)
        }
    
        const response = await api.post(DOCUMENTS_URL, formData, {
            headers: {
                "Content-Type": "multipart/form-data",
            },
        })
    
        return response.data
    },

    // Update document metadata
    updateDocumentMetadata: async (id, title, description) => {
        const formData = new FormData()
        formData.append("title", title)
    
        if (description) {
            formData.append("description", description)
        }
    
        const response = await api.put(`${DOCUMENTS_URL}/${id}`, formData)
        return response.data
    },  

    // Update document status
    updateDocumentStatus: async (id, status, comments) => {
        const params = { status }
    
        if (comments) {
            params.comments = comments
        }
    
        const response = await api.put(`${DOCUMENTS_URL}/${id}/status`, null, {
            params,
        })
    
        return response.data
    },

    // Delete document
    deleteDocument: async (id) => {
        await api.delete(`${DOCUMENTS_URL}/${id}`)
    },
}

