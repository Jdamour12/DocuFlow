import axios from "axios"

const API_URL = "/api"

const api = axios.create({
    baseURL: API_URL,
    headers: {
        "Content-Type": "application/json",
    },
})

// Add request interceptor for authentication (to be implemented later)
api.interceptors.request.use(
    (config) => {
      // Will add auth token here when security is implemented
        return config
    },
    (error) => {
        return Promise.reject(error)
    },
)

// Add response interceptor for error handling
api.interceptors.response.use(
    (response) => {
        return response
    },
    (error) => {
    // Handle errors globally
    console.error("API Error:", error)
    return Promise.reject(error)
    },
)

export default api

