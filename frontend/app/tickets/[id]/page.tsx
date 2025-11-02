'use client';

import { useEffect, useState } from 'react';
import { useRouter, useParams } from 'next/navigation';
import { ticketAPI, Ticket } from '@/lib/api';
import Link from 'next/link';

export default function TicketDetailPage() {
  const [ticket, setTicket] = useState<Ticket | null>(null);
  const [loading, setLoading] = useState(true);
  const [comment, setComment] = useState('');
  const [newStatus, setNewStatus] = useState('');
  const [rating, setRating] = useState(0);
  const [feedback, setFeedback] = useState('');
  const [file, setFile] = useState<File | null>(null);
  const [user, setUser] = useState<any>(null);
  const router = useRouter();
  const params = useParams();
  const id = params.id as string;

  useEffect(() => {
    const token = localStorage.getItem('token');
    const userData = localStorage.getItem('user');
    
    if (!token) {
      router.push('/login');
      return;
    }

    if (userData) {
      setUser(JSON.parse(userData));
    }

    fetchTicket();
  }, [id, router]);

  const fetchTicket = async () => {
    try {
      const response = await ticketAPI.getById(Number(id));
      setTicket(response.data);
      setNewStatus(response.data.status);
    } catch (error) {
      console.error('Failed to fetch ticket', error);
    } finally {
      setLoading(false);
    }
  };

  const handleAddComment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!comment.trim()) return;

    try {
      await ticketAPI.addComment(Number(id), comment);
      setComment('');
      fetchTicket();
    } catch (error) {
      console.error('Failed to add comment', error);
    }
  };

  const handleStatusUpdate = async () => {
    try {
      await ticketAPI.updateStatus(Number(id), newStatus);
      fetchTicket();
    } catch (error) {
      console.error('Failed to update status', error);
    }
  };

  const handleRateTicket = async (e: React.FormEvent) => {
    e.preventDefault();
    if (rating === 0) {
      alert('Please select a rating');
      return;
    }

    try {
      await ticketAPI.rate(Number(id), rating, feedback);
      fetchTicket();
      setRating(0);
      setFeedback('');
    } catch (error: any) {
      alert(error.response?.data?.message || 'Failed to rate ticket');
    }
  };

  const handleFileUpload = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!file) return;

    try {
      await ticketAPI.uploadAttachment(Number(id), file);
      setFile(null);
      fetchTicket();
    } catch (error) {
      console.error('Failed to upload file', error);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-xl">Loading...</div>
      </div>
    );
  }

  if (!ticket) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-xl">Ticket not found</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-100">
      <nav className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <h1 className="text-xl font-bold">Ticket #{ticket.id}</h1>
            </div>
            <div className="flex items-center">
              <Link
                href="/dashboard"
                className="px-4 py-2 bg-gray-500 text-white rounded hover:bg-gray-600"
              >
                Back to Dashboard
              </Link>
            </div>
          </div>
        </div>
      </nav>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2">
            <div className="bg-white p-6 rounded-lg shadow-md mb-6">
              <h2 className="text-2xl font-bold mb-4">{ticket.subject}</h2>
              <div className="mb-4">
                <span className="text-gray-600">Priority: </span>
                <span className="font-semibold">{ticket.priority}</span>
              </div>
              <div className="mb-4">
                <span className="text-gray-600">Status: </span>
                <span className="font-semibold">{ticket.status.replace('_', ' ')}</span>
              </div>
              <div className="mb-4">
                <span className="text-gray-600">Created by: </span>
                <span className="font-semibold">{ticket.creator.username}</span>
              </div>
              {ticket.assignee && (
                <div className="mb-4">
                  <span className="text-gray-600">Assigned to: </span>
                  <span className="font-semibold">{ticket.assignee.username}</span>
                </div>
              )}
              <div className="mb-4">
                <span className="text-gray-600">Created: </span>
                <span>{new Date(ticket.createdAt).toLocaleString()}</span>
              </div>
              <div className="mt-6">
                <h3 className="font-bold mb-2">Description:</h3>
                <p className="text-gray-700 whitespace-pre-wrap">{ticket.description}</p>
              </div>
            </div>

            {ticket.attachments && ticket.attachments.length > 0 && (
              <div className="bg-white p-6 rounded-lg shadow-md mb-6">
                <h3 className="font-bold mb-4">Attachments</h3>
                <ul className="space-y-2">
                  {ticket.attachments.map((attachment) => (
                    <li key={attachment.id} className="flex items-center space-x-2">
                      <span className="text-blue-600">{attachment.fileName}</span>
                      <span className="text-gray-500 text-sm">
                        ({(attachment.fileSize / 1024).toFixed(2)} KB)
                      </span>
                    </li>
                  ))}
                </ul>
              </div>
            )}

            <div className="bg-white p-6 rounded-lg shadow-md mb-6">
              <h3 className="font-bold mb-4">Comments</h3>
              <div className="space-y-4 mb-6">
                {ticket.comments && ticket.comments.length > 0 ? (
                  ticket.comments.map((comment) => (
                    <div key={comment.id} className="border-l-4 border-blue-500 pl-4 py-2">
                      <div className="flex items-center space-x-2 mb-2">
                        <span className="font-semibold">{comment.user.username}</span>
                        <span className="text-gray-500 text-sm">
                          {new Date(comment.createdAt).toLocaleString()}
                        </span>
                      </div>
                      <p className="text-gray-700">{comment.content}</p>
                    </div>
                  ))
                ) : (
                  <p className="text-gray-500">No comments yet</p>
                )}
              </div>

              <form onSubmit={handleAddComment}>
                <textarea
                  value={comment}
                  onChange={(e) => setComment(e.target.value)}
                  placeholder="Add a comment..."
                  rows={3}
                  className="w-full px-3 py-2 border border-gray-300 rounded focus:outline-none focus:border-blue-500 mb-2"
                />
                <button
                  type="submit"
                  className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
                >
                  Add Comment
                </button>
              </form>
            </div>
          </div>

          <div className="lg:col-span-1">
            <div className="bg-white p-6 rounded-lg shadow-md mb-6">
              <h3 className="font-bold mb-4">Update Status</h3>
              <select
                value={newStatus}
                onChange={(e) => setNewStatus(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded mb-4"
              >
                <option value="OPEN">Open</option>
                <option value="IN_PROGRESS">In Progress</option>
                <option value="RESOLVED">Resolved</option>
                <option value="CLOSED">Closed</option>
              </select>
              <button
                onClick={handleStatusUpdate}
                className="w-full px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600"
              >
                Update Status
              </button>
            </div>

            <div className="bg-white p-6 rounded-lg shadow-md mb-6">
              <h3 className="font-bold mb-4">Upload Attachment</h3>
              <form onSubmit={handleFileUpload}>
                <input
                  type="file"
                  onChange={(e) => setFile(e.target.files?.[0] || null)}
                  className="w-full mb-4"
                />
                <button
                  type="submit"
                  disabled={!file}
                  className="w-full px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 disabled:bg-gray-400"
                >
                  Upload
                </button>
              </form>
            </div>

            {(ticket.status === 'RESOLVED' || ticket.status === 'CLOSED') &&
              ticket.creator.id === user?.userId &&
              !ticket.rating && (
                <div className="bg-white p-6 rounded-lg shadow-md">
                  <h3 className="font-bold mb-4">Rate This Ticket</h3>
                  <form onSubmit={handleRateTicket}>
                    <div className="mb-4">
                      <label className="block text-sm font-medium mb-2">Rating</label>
                      <div className="flex space-x-2">
                        {[1, 2, 3, 4, 5].map((star) => (
                          <button
                            key={star}
                            type="button"
                            onClick={() => setRating(star)}
                            className={`text-2xl ${
                              star <= rating ? 'text-yellow-500' : 'text-gray-300'
                            }`}
                          >
                            ★
                          </button>
                        ))}
                      </div>
                    </div>
                    <div className="mb-4">
                      <label className="block text-sm font-medium mb-2">
                        Feedback (Optional)
                      </label>
                      <textarea
                        value={feedback}
                        onChange={(e) => setFeedback(e.target.value)}
                        rows={3}
                        className="w-full px-3 py-2 border border-gray-300 rounded"
                      />
                    </div>
                    <button
                      type="submit"
                      className="w-full px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
                    >
                      Submit Rating
                    </button>
                  </form>
                </div>
              )}

            {ticket.rating && (
              <div className="bg-white p-6 rounded-lg shadow-md">
                <h3 className="font-bold mb-4">Rating</h3>
                <div className="flex space-x-1 mb-2">
                  {[1, 2, 3, 4, 5].map((star) => (
                    <span
                      key={star}
                      className={`text-2xl ${
                        star <= ticket.rating! ? 'text-yellow-500' : 'text-gray-300'
                      }`}
                    >
                      ★
                    </span>
                  ))}
                </div>
                {ticket.feedback && (
                  <p className="text-gray-700 mt-2">{ticket.feedback}</p>
                )}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
