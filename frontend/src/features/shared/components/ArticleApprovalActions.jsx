/**
 * Shared Article Approval Actions Component
 * Reusable component for approve/reject modals and handlers
 */

import React, { useState } from 'react';
import { ConfirmModal, Modal } from '../../../components/common';

/**
 * ArticleApprovalActions component
 * @param {Object} props
 * @param {Object} props.approveConfirmation - Approve confirmation state: { isOpen, articleId, articleTitle }
 * @param {Function} props.onApproveConfirm - Approve confirm handler
 * @param {Function} props.onApproveCancel - Approve cancel handler
 * @param {Object} props.rejectConfirmation - Reject confirmation state: { isOpen, articleId, articleTitle, reason }
 * @param {Function} props.onRejectConfirm - Reject confirm handler
 * @param {Function} props.onRejectCancel - Reject cancel handler
 * @param {Function} props.onRejectReasonChange - Reject reason change handler
 * @param {string} props.error - Error message to display
 */
function ArticleApprovalActions({
  approveConfirmation = { isOpen: false, articleId: null, articleTitle: '' },
  onApproveConfirm,
  onApproveCancel,
  rejectConfirmation = { isOpen: false, articleId: null, articleTitle: '', reason: '' },
  onRejectConfirm,
  onRejectCancel,
  onRejectReasonChange,
  error
}) {
  const rejectReason = rejectConfirmation.reason || '';
  const isValidReason = rejectReason.trim().length >= 10 && rejectReason.trim().length <= 1000;

  return (
    <>
      {/* Approve Confirmation Modal */}
      <ConfirmModal
        isOpen={approveConfirmation.isOpen}
        onClose={onApproveCancel}
        onConfirm={onApproveConfirm}
        title="Approve Article"
        message={
          <div className="space-y-2">
            <p className="text-gray-700">
              Are you sure you want to approve <strong>"{approveConfirmation.articleTitle}"</strong>?
            </p>
            <p className="text-sm text-green-600 font-medium">
              This article will be published and visible to all users.
            </p>
          </div>
        }
        confirmText="Approve"
        cancelText="Cancel"
        variant="success"
      />

      {/* Reject Confirmation Modal */}
      {rejectConfirmation.isOpen && (
        <Modal
          isOpen={rejectConfirmation.isOpen}
          onClose={onRejectCancel}
          title="Reject Article"
          size="md"
          footer={
            <>
              <button
                onClick={onRejectCancel}
                className="px-4 py-2 text-gray-700 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors"
              >
                Cancel
              </button>
              <button
                onClick={onRejectConfirm}
                disabled={!isValidReason}
                className="px-4 py-2 bg-red-500 text-white hover:bg-red-600 rounded-lg transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                Reject Article
              </button>
            </>
          }
        >
          <div className="space-y-4">
            <p className="text-gray-700">
              Are you sure you want to reject <strong>"{rejectConfirmation.articleTitle}"</strong>?
            </p>
            
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Rejection Reason <span className="text-red-500">*</span>
              </label>
              <textarea
                value={rejectReason}
                onChange={(e) => onRejectReasonChange && onRejectReasonChange(e.target.value)}
                placeholder="Enter reason for rejection (minimum 10 characters)..."
                rows={4}
                className={`w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500 ${
                  rejectReason && rejectReason.trim().length > 0 && 
                  rejectReason.trim().length < 10 
                    ? 'border-red-300' 
                    : 'border-gray-300'
                }`}
                minLength={10}
                maxLength={1000}
              />
              <div className="mt-1 text-sm text-gray-500">
                {rejectReason && rejectReason.trim().length > 0 && 
                 rejectReason.trim().length < 10 
                  ? `Minimum 10 characters required (${rejectReason.trim().length}/10)`
                  : `${rejectReason.length}/1000 characters`}
              </div>
              {rejectReason && rejectReason.trim().length > 0 && 
               rejectReason.trim().length < 10 && (
                <p className="mt-1 text-sm text-red-600">
                  Rejection reason must be at least 10 characters
                </p>
              )}
              {error && (
                <p className="mt-1 text-sm text-red-600">{error}</p>
              )}
            </div>
          </div>
        </Modal>
      )}
    </>
  );
}

export default ArticleApprovalActions;



