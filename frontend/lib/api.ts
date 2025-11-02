import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export interface User {
  id: number;
  username: string;
  email: string;
  fullName?: string;
  role: 'USER' | 'SUPPORT_AGENT' | 'ADMIN';
  active: boolean;
  createdAt: string;
}

export interface Ticket {
  id: number;
  subject: string;
  description: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';
  status: 'OPEN' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED';
  creator: User;
  assignee?: User;
  comments: Comment[];
  attachments: Attachment[];
  rating?: number;
  feedback?: string;
  createdAt: string;
  updatedAt: string;
  resolvedAt?: string;
  closedAt?: string;
}

export interface Comment {
  id: number;
  content: string;
  user: User;
  createdAt: string;
}

export interface Attachment {
  id: number;
  fileName: string;
  fileType: string;
  fileSize: number;
  user: User;
  uploadedAt: string;
}

export const authAPI = {
  login: (username: string, password: string) =>
    api.post('/auth/login', { username, password }),
  register: (username: string, email: string, password: string, fullName?: string) =>
    api.post('/auth/register', { username, email, password, fullName }),
};

export const ticketAPI = {
  getAll: () => api.get<Ticket[]>('/tickets'),
  getById: (id: number) => api.get<Ticket>(`/tickets/${id}`),
  create: (subject: string, description: string, priority: string) =>
    api.post<Ticket>('/tickets', { subject, description, priority }),
  updateStatus: (id: number, status: string) =>
    api.put<Ticket>(`/tickets/${id}/status`, { status }),
  assign: (id: number, assigneeId: number) =>
    api.put<Ticket>(`/tickets/${id}/assign`, { assigneeId }),
  addComment: (id: number, content: string) =>
    api.post<Comment>(`/tickets/${id}/comments`, { content }),
  rate: (id: number, rating: number, feedback?: string) =>
    api.post<Ticket>(`/tickets/${id}/rate`, { rating, feedback }),
  uploadAttachment: (id: number, file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post<Attachment>(`/tickets/${id}/attachments`, formData);
  },
  search: (keyword: string) => api.get<Ticket[]>(`/tickets/search?keyword=${keyword}`),
  filterByStatus: (status: string) =>
    api.get<Ticket[]>(`/tickets/filter/status?status=${status}`),
  filterByPriority: (priority: string) =>
    api.get<Ticket[]>(`/tickets/filter/priority?priority=${priority}`),
};

export const adminAPI = {
  getAllUsers: () => api.get<User[]>('/admin/users'),
  getUserById: (id: number) => api.get<User>(`/admin/users/${id}`),
  createUser: (user: Partial<User>) => api.post<User>('/admin/users', user),
  updateUser: (id: number, user: Partial<User>) => api.put<User>(`/admin/users/${id}`, user),
  deleteUser: (id: number) => api.delete(`/admin/users/${id}`),
  getAllTickets: () => api.get<Ticket[]>('/admin/tickets'),
  forceUpdateTicketStatus: (id: number, status: string) =>
    api.put<Ticket>(`/admin/tickets/${id}/status`, { status }),
  forceAssignTicket: (id: number, assigneeId: number) =>
    api.put<Ticket>(`/admin/tickets/${id}/assign`, { assigneeId }),
};

export default api;
