/**
 * Audio Generation Modal Component
 * Allows authors to generate TTS audio for their articles
 */

import React, { useState, useEffect } from 'react';
import { Volume2, Loader2, CheckCircle, XCircle } from 'lucide-react';
import { Input, Modal, Button, Alert } from '../../../components/common';
import newsService from '../../article/api';

/**
 * AudioGenerationModal component
 * @param {Object} props
 * @param {boolean} props.isOpen - Whether the modal is open
 * @param {Function} props.onClose - Callback when modal is closed
 * @param {number} props.articleId - Article ID to generate audio for
 * @param {string} props.articleTitle - Article title (for display)
 * @param {Function} props.onSuccess - Callback when audio generation starts successfully
 */
function AudioGenerationModal({ isOpen, onClose, articleId, articleTitle, onSuccess }) {
  const [useDefaultConfig, setUseDefaultConfig] = useState(true);
  const [isGenerating, setIsGenerating] = useState(false);
  const [error, setError] = useState(null);
  const [audioStatus, setAudioStatus] = useState(null); // 'GENERATING_AUDIO' | 'COMPLETED' | 'FAILED'
  const [audioProgress, setAudioProgress] = useState(0);
  const [audioFileId, setAudioFileId] = useState(null);
  const [pollingInterval, setPollingInterval] = useState(null);
  const [customVoiceSettings, setCustomVoiceSettings] = useState({
    languageCode: 'en-US',
    voiceName: 'en-US-Standard-B',
    speakingRate: 1.0,
    pitch: 0.0,
    volumeGain: 0.0,
    audioEncoding: 'MP3',
    sampleRateHertz: 'RATE_24000'
  });

  const handleGenerateAudio = async () => {
    if (!articleId) {
      setError('Article ID is required');
      return;
    }

    setIsGenerating(true);
    setError(null);

    try {
      const options = {
        enableSummarization: true,
        enableTranslation: false
      };

      // Add custom voice settings if not using default
      if (!useDefaultConfig) {
        options.customVoiceSettings = customVoiceSettings;
      }

      const result = await newsService.generateAudio(articleId, options);

      if (result.success) {
        // Get audio file ID from response
        const audioFileId = result.data?.id || result.data?.audioFileId;
        
        if (audioFileId) {
          setAudioFileId(audioFileId);
          setAudioStatus('GENERATING_AUDIO');
          setAudioProgress(0);
          
          // Start polling for status
          startStatusPolling(audioFileId);
        } else {
          // If no audio file ID, assume success and close
          if (onSuccess) {
            onSuccess(result.data);
          }
          onClose();
        }
      } else {
        // Handle different error cases
        let errorMsg = result.error || 'Failed to generate audio';
        
        if (result.errorCode === 5008 || result.status === 403) {
          errorMsg = 'Only the article author can generate TTS audio.';
        } else if (result.status === 404) {
          errorMsg = 'Endpoint not found. The audio generation feature may not be available yet.';
        } else if (result.errorCode === 'TTS_CONFIG_NO_DEFAULT') {
          errorMsg = 'No default TTS configuration found. Please provide custom voice settings or configure your default TTS settings.';
        }
        
        setError(errorMsg);
      }
    } catch (err) {
      console.error('Error generating audio:', err);
      setError('An unexpected error occurred. Please try again.');
    } finally {
      setIsGenerating(false);
    }
  };

  const handleGenerateFromSummary = async () => {
    if (!articleId) {
      setError('Article ID is required');
      return;
    }

    setIsGenerating(true);
    setError(null);

    try {
      const options = {};

      // Add custom voice settings if not using default
      if (!useDefaultConfig) {
        options.customVoiceSettings = customVoiceSettings;
      }

      const result = await newsService.generateAudioFromSummary(articleId, options);

      if (result.success) {
        // Get audio file ID from response
        const audioFileId = result.data?.id || result.data?.audioFileId;
        
        if (audioFileId) {
          setAudioFileId(audioFileId);
          setAudioStatus('GENERATING_AUDIO');
          setAudioProgress(0);
          
          // Start polling for status
          startStatusPolling(audioFileId);
        } else {
          // If no audio file ID, assume success and close
          if (onSuccess) {
            onSuccess(result.data);
          }
          onClose();
        }
      } else {
        let errorMsg = result.error || 'Failed to generate audio from summary';
        
        if (result.errorCode === 5008 || result.status === 403) {
          errorMsg = 'Only the article author can generate TTS audio.';
        } else if (result.status === 404) {
          errorMsg = 'Endpoint not found. The audio generation feature may not be available yet.';
        } else if (result.errorCode === 'TTS_CONFIG_NO_DEFAULT') {
          errorMsg = 'No default TTS configuration found. Please provide custom voice settings or configure your default TTS settings.';
        }
        
        setError(errorMsg);
      }
    } catch (err) {
      console.error('Error generating audio from summary:', err);
      setError('An unexpected error occurred. Please try again.');
    } finally {
      setIsGenerating(false);
    }
  };

  // Poll audio generation status
  const startStatusPolling = (fileId) => {
    let isPolling = true;
    
    const pollInterval = setInterval(async () => {
      if (!isPolling) return;
      
      try {
        const statusResult = await newsService.checkAudioStatus(fileId);
        
        if (statusResult.success && statusResult.data) {
          const status = statusResult.data.status;
          const progress = statusResult.data.progressPercentage;
          
          setAudioStatus(status);
          if (progress !== null && progress !== undefined) {
            setAudioProgress(progress);
          }
          
          if (status === 'COMPLETED') {
            isPolling = false;
            clearInterval(pollInterval);
            setPollingInterval(null);
            
            if (onSuccess) {
              onSuccess({ id: fileId, status: 'COMPLETED' });
            }
            
            // Auto close after 2 seconds
            setTimeout(() => {
              onClose();
              // Reset state
              setAudioStatus(null);
              setAudioProgress(0);
              setAudioFileId(null);
            }, 2000);
          } else if (status === 'FAILED') {
            const errorMsg = statusResult.data.errorMessage || 'Audio generation failed';
            setError(`Audio generation failed: ${errorMsg}`);
            isPolling = false;
            clearInterval(pollInterval);
            setPollingInterval(null);
            setAudioStatus('FAILED');
            
            // Reset after 5 seconds
            setTimeout(() => {
              setAudioStatus(null);
              setAudioProgress(0);
              setAudioFileId(null);
            }, 5000);
          }
        }
      } catch (error) {
        console.error('Error polling audio status:', error);
        // Continue polling even on error
      }
    }, 3000); // Poll every 3 seconds
    
    setPollingInterval(pollInterval);
    
    // Cleanup: stop polling after 5 minutes (safety timeout)
    setTimeout(() => {
      if (isPolling) {
        isPolling = false;
        clearInterval(pollInterval);
        setPollingInterval(null);
        setError('Status check timeout. Please refresh the page to check status manually.');
      }
    }, 300000); // 5 minutes
  };

  // Cleanup polling on unmount or close
  useEffect(() => {
    return () => {
      if (pollingInterval) {
        clearInterval(pollingInterval);
      }
    };
  }, [pollingInterval]);

  // Reset state when modal closes
  useEffect(() => {
    if (!isOpen) {
      if (pollingInterval) {
        clearInterval(pollingInterval);
        setPollingInterval(null);
      }
      setAudioStatus(null);
      setAudioProgress(0);
      setAudioFileId(null);
      setError(null);
      setIsGenerating(false);
    }
  }, [isOpen, pollingInterval]);

  const modalTitle = (
    <div>
      <h2 className="text-xl font-bold text-gray-900">Generate Audio</h2>
      {articleTitle && (
        <p className="text-sm text-gray-500 mt-1">{articleTitle}</p>
      )}
    </div>
  );

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={modalTitle}
      size="lg"
      closeOnOverlay={!isGenerating}
      footer={
        <>
          <Button
            variant="outline"
            onClick={onClose}
            disabled={isGenerating || audioStatus === 'GENERATING_AUDIO'}
          >
            {audioStatus === 'GENERATING_AUDIO' ? 'Processing...' : 'Cancel'}
          </Button>
          {audioStatus !== 'GENERATING_AUDIO' && audioStatus !== 'COMPLETED' && (
            <>
              <Button
                variant="secondary"
                onClick={handleGenerateFromSummary}
                isLoading={isGenerating}
                disabled={isGenerating}
              >
                <Volume2 className="h-4 w-4 mr-2" />
                Generate from Summary
              </Button>
              <Button
                variant="primary"
                onClick={handleGenerateAudio}
                isLoading={isGenerating}
                disabled={isGenerating}
              >
                <Volume2 className="h-4 w-4 mr-2" />
                Generate Full Audio
              </Button>
            </>
          )}
        </>
      }
    >
      {error && (
        <Alert variant="error" title="Error" dismissible onDismiss={() => setError(null)}>
          {error}
        </Alert>
      )}

      {/* Audio Generation Progress */}
      {audioStatus === 'GENERATING_AUDIO' && (
        <div className="mb-6 bg-blue-50 border border-blue-200 rounded-lg p-4">
          <div className="flex items-center gap-3 mb-3">
            <Loader2 className="h-5 w-5 animate-spin text-blue-500" />
            <h3 className="text-base font-semibold text-gray-900">Generating Audio...</h3>
          </div>
          <p className="text-sm text-gray-600 mb-3">
            Your audio is being generated. This may take a few minutes. Please keep this window open.
          </p>
          {/* Progress Bar */}
          <div className="w-full bg-gray-200 rounded-full h-2.5 mb-2">
            <div
              className="bg-blue-500 h-2.5 rounded-full transition-all duration-300"
              style={{ width: `${audioProgress}%` }}
            ></div>
          </div>
          <p className="text-xs text-gray-500">
            {audioProgress > 0 ? `${audioProgress.toFixed(1)}% complete` : 'Starting...'}
          </p>
        </div>
      )}

      {/* Audio Generation Completed */}
      {audioStatus === 'COMPLETED' && (
        <div className="mb-6 bg-green-50 border border-green-200 rounded-lg p-4">
          <div className="flex items-center gap-3 mb-2">
            <CheckCircle className="h-5 w-5 text-green-500" />
            <h3 className="text-base font-semibold text-gray-900">Audio Generation Completed!</h3>
          </div>
          <p className="text-sm text-gray-600">
            Your audio has been generated successfully. This window will close automatically.
          </p>
        </div>
      )}

      {/* Audio Generation Failed */}
      {audioStatus === 'FAILED' && (
        <div className="mb-6 bg-red-50 border border-red-200 rounded-lg p-4">
          <div className="flex items-center gap-3 mb-2">
            <XCircle className="h-5 w-5 text-red-500" />
            <h3 className="text-base font-semibold text-gray-900">Audio Generation Failed</h3>
          </div>
          <p className="text-sm text-gray-600">
            The audio generation process failed. Please try again.
          </p>
        </div>
      )}

      <div className="space-y-6">
        {/* Option Selection */}
        <div>
                <label className="block text-sm font-medium text-gray-700 mb-3">
                  Voice Configuration
                </label>
                <div className="space-y-3">
                  <label className="flex items-center p-4 border rounded-lg cursor-pointer hover:bg-gray-50 transition-colors">
                    <input
                      type="radio"
                      name="voiceConfig"
                      checked={useDefaultConfig}
                      onChange={() => setUseDefaultConfig(true)}
                      className="mr-3"
                      disabled={isGenerating}
                    />
                    <div>
                      <div className="font-medium text-gray-900">Use Default TTS Configuration</div>
                      <div className="text-sm text-gray-500 mt-1">
                        Use your saved default TTS settings
                      </div>
                    </div>
                  </label>
                  <label className="flex items-center p-4 border rounded-lg cursor-pointer hover:bg-gray-50 transition-colors">
                    <input
                      type="radio"
                      name="voiceConfig"
                      checked={!useDefaultConfig}
                      onChange={() => setUseDefaultConfig(false)}
                      className="mr-3"
                      disabled={isGenerating}
                    />
                    <div>
                      <div className="font-medium text-gray-900">Custom Voice Settings</div>
                      <div className="text-sm text-gray-500 mt-1">
                        Configure voice settings for this audio generation
                      </div>
                    </div>
                  </label>
                </div>
              </div>

              {/* Custom Voice Settings Form */}
              {!useDefaultConfig && (
                <div className="border-t pt-6 space-y-4">
                  <h3 className="text-lg font-semibold text-gray-900 mb-4">Custom Voice Settings</h3>
                  
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <Input
                      label="Language Code"
                      type="text"
                      value={customVoiceSettings.languageCode}
                      onChange={(e) => setCustomVoiceSettings({
                        ...customVoiceSettings,
                        languageCode: e.target.value
                      })}
                      placeholder="en-US"
                      required
                      helperText="Format: xx-XX (e.g., en-US, vi-VN)"
                      disabled={isGenerating}
                    />
                    
                    <Input
                      label="Voice Name"
                      type="text"
                      value={customVoiceSettings.voiceName}
                      onChange={(e) => setCustomVoiceSettings({
                        ...customVoiceSettings,
                        voiceName: e.target.value
                      })}
                      placeholder="en-US-Standard-B"
                      required
                      helperText="Google TTS voice name (max 50 characters)"
                      disabled={isGenerating}
                    />
                    
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Speaking Rate: <span className="font-semibold text-orange-600">{customVoiceSettings.speakingRate}</span>
                      </label>
                      <input
                        type="range"
                        min="0.25"
                        max="4.0"
                        step="0.1"
                        value={customVoiceSettings.speakingRate}
                        onChange={(e) => setCustomVoiceSettings({
                          ...customVoiceSettings,
                          speakingRate: parseFloat(e.target.value)
                        })}
                        className="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-orange-500"
                        disabled={isGenerating}
                      />
                      <div className="flex justify-between text-xs text-gray-500 mt-1">
                        <span>0.25</span>
                        <span>4.0</span>
                      </div>
                    </div>
                    
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Pitch: <span className="font-semibold text-orange-600">{customVoiceSettings.pitch}</span>
                      </label>
                      <input
                        type="range"
                        min="-20.0"
                        max="20.0"
                        step="0.1"
                        value={customVoiceSettings.pitch}
                        onChange={(e) => setCustomVoiceSettings({
                          ...customVoiceSettings,
                          pitch: parseFloat(e.target.value)
                        })}
                        className="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-orange-500"
                        disabled={isGenerating}
                      />
                      <div className="flex justify-between text-xs text-gray-500 mt-1">
                        <span>-20.0</span>
                        <span>0</span>
                        <span>20.0</span>
                      </div>
                    </div>
                    
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Volume Gain (dB): <span className="font-semibold text-orange-600">{customVoiceSettings.volumeGain}</span>
                      </label>
                      <input
                        type="range"
                        min="-96.0"
                        max="16.0"
                        step="0.1"
                        value={customVoiceSettings.volumeGain}
                        onChange={(e) => setCustomVoiceSettings({
                          ...customVoiceSettings,
                          volumeGain: parseFloat(e.target.value)
                        })}
                        className="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-orange-500"
                        disabled={isGenerating}
                      />
                      <div className="flex justify-between text-xs text-gray-500 mt-1">
                        <span>-96.0</span>
                        <span>0</span>
                        <span>16.0</span>
                      </div>
                    </div>
                    
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Audio Encoding
                      </label>
                      <select
                        value={customVoiceSettings.audioEncoding}
                        onChange={(e) => setCustomVoiceSettings({
                          ...customVoiceSettings,
                          audioEncoding: e.target.value
                        })}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
                        disabled={isGenerating}
                      >
                        <option value="MP3">MP3</option>
                        <option value="LINEAR16">LINEAR16</option>
                        <option value="OGG_OPUS">OGG_OPUS</option>
                      </select>
                    </div>
                    
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        Sample Rate (Hz) <span className="text-red-500">*</span>
                      </label>
                      <select
                        value={customVoiceSettings.sampleRateHertz}
                        onChange={(e) => setCustomVoiceSettings({
                          ...customVoiceSettings,
                          sampleRateHertz: e.target.value
                        })}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
                        required
                        disabled={isGenerating}
                      >
                        <option value="RATE_8000">8000 Hz</option>
                        <option value="RATE_16000">16000 Hz</option>
                        <option value="RATE_22050">22050 Hz</option>
                        <option value="RATE_24000">24000 Hz</option>
                        <option value="RATE_44100">44100 Hz</option>
                        <option value="RATE_48000">48000 Hz</option>
                      </select>
                    </div>
                  </div>
                </div>
              )}
        </div>
    </Modal>
  );
}

export default AudioGenerationModal;

