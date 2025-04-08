"use client"

import { useState } from "react"
import {
    AppBar,
    Toolbar,
    Typography,
    Container,
    Box,
    Drawer,
    List,
    ListItem,
    ListItemIcon,
    ListItemText,
    Divider,
    IconButton,
} from "@mui/material"
import { Dashboard as DashboardIcon, CloudUpload as UploadIcon, Menu as MenuIcon } from "@mui/icons-material"
import { Link, useLocation } from "react-router-dom"

const Layout = ({ children }) => {
    const [drawerOpen, setDrawerOpen] = useState(false)
    const location = useLocation()

    const toggleDrawer = () => {
        setDrawerOpen(!drawerOpen)
    }

    const menuItems = [
        { text: "Dashboard", icon: <DashboardIcon />, path: "/" },
        { text: "Upload Document", icon: <UploadIcon />, path: "/upload" },
    ]  

    const drawer = (
        <Box sx={{ width: 250 }} role="presentation">
            <Box sx={{ p: 2 }}>
                <Typography variant="h6" component="div">
                    DocuFlow
                </Typography>
                <Typography variant="body2" color="text.secondary">
                    Document Management System
                </Typography>
            </Box>
            <Divider />
            <List>
                {menuItems.map((item) => (
                    <ListItem
                        button
                        key={item.text}
                        component={Link}
                        to={item.path}
                        selected={location.pathname === item.path}
                        onClick={() => setDrawerOpen(false)}
                    >  
                        <ListItemIcon>{item.icon}</ListItemIcon>
                        <ListItemText primary={item.text} />
                    </ListItem>
                ))}
            </List>
        </Box>
    )

return (
    <Box sx={{ display: "flex", flexDirection: "column", minHeight: "100vh" }}>
        <AppBar position="static">
            <Toolbar>
                <IconButton color="inherit" edge="start" onClick={toggleDrawer} sx={{ mr: 2 }}>
                <MenuIcon />
                </IconButton>
                <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                DocuFlow
                </Typography>
                <Box>
                {/* Placeholder for user menu/profile */}
                <Typography variant="body1">User: current-user</Typography>
                </Box>
            </Toolbar>
        </AppBar>
        
    <Drawer anchor="left" open={drawerOpen} onClose={() => setDrawerOpen(false)}>
        {drawer}
        </Drawer>

        <Container component="main" sx={{ flexGrow: 1, py: 3 }}>
            {children}
        </Container>

        <Box component="footer" sx={{ py: 3, bgcolor: "background.paper", mt: "auto" }}>
            <Container maxWidth="lg">
            <Typography variant="body2" color="text.secondary" align="center">
            DocuFlow Document Management System © {new Date().getFullYear()}
            </Typography>
        </Container>
        </Box>
    </Box>
)
}

export default Layout