# ScrollGuard App Lifecycle Documentation

## Overview

ScrollGuard is a Kotlin Multiplatform mobile application designed to monitor social media usage and detect doomscrolling patterns. The app runs on Android and iOS platforms with shared business logic and platform-specific implementations.

## Application Architecture

### Core Components

- **MainActivity**: Entry point for Android application
- **ScrollGuardAccessibilityService**: Background service for monitoring user interactions
- **ScrollGuardMonitoringService**: Foreground service for real-time usage tracking
- **CleanScrollGuardApp**: Main UI component with Compose Multiplatform
- **PermissionManager**: Handles all app permissions
- **UsageStatsManager**: Manages usage statistics collection

## Application Lifecycle States

### 1. Application Startup

```
App Launch → MainActivity.onCreate() → Koin DI Initialization → UI Composition
```

#### MainActivity Lifecycle

- **onCreate()**:
  - Enables edge-to-edge display
  - Initializes Koin dependency injection
  - Sets Compose UI content
  - Handles error recovery for DI initialization

#### Dependency Injection Setup

- **Koin Module Loading**:
  - Common module: Repository implementations
  - Platform module: Platform-specific services
  - Android module: Android-specific implementations

### 2. Permission Management Lifecycle

The app requires three critical permissions for monitoring functionality:

#### Permission States

- **Usage Stats Permission**: Required for app usage tracking
- **Accessibility Permission**: Required for scroll event detection
- **Overlay Permission**: Required for intervention notifications

#### Permission Flow

```
App Start → Permission Check → Request Missing Permissions → Grant → Enable Monitoring
```

### 3. Service Lifecycle

#### ScrollGuardAccessibilityService

**Lifecycle States:**

- **Service Connected**: `onServiceConnected()` - Initialize monitoring
- **Event Processing**: `onAccessibilityEvent()` - Handle scroll and app switch events
- **Service Interrupted**: `onInterrupt()` - Handle service interruption
- **Service Destroyed**: `onDestroy()` - Cleanup resources

**Event Handling:**

- `TYPE_WINDOW_STATE_CHANGED`: App switching detection
- `TYPE_VIEW_SCROLLED`: Scroll event tracking

#### ScrollGuardMonitoringService

**Lifecycle States:**

- **Service Created**: `onCreate()` - Initialize notification manager and channels
- **Service Started**: `onStartCommand()` - Handle start/stop monitoring actions
- **Monitoring Active**: `startMonitoring()` - Begin foreground monitoring
- **Monitoring Stopped**: `stopMonitoring()` - Stop monitoring and cleanup
- **Service Destroyed**: `onDestroy()` - Cancel coroutines and cleanup

**Monitoring Loop:**

```
Start Monitoring → Check Session Data → Evaluate Intervention Rules → Trigger Notifications → Repeat
```

### 4. UI Lifecycle (Compose)

#### CleanScrollGuardApp States

The UI follows a state-driven architecture with the following states:

**UIState Components:**

- **Loading State**: During permission checks and data loading
- **Permission Status**: Shows which permissions are granted/missing
- **Protection Status**: Indicates if monitoring is active
- **Usage Statistics**: Displays app usage data
- **Error State**: Shows error messages and recovery options

**State Transitions:**

```
Initial Loading → Permission Check → Protection Toggle → Usage Display → Error Handling
```

#### ViewModel Lifecycle

**CleanScrollGuardViewModel:**

- **Initialization**: `init()` - Check permissions on creation
- **Permission Management**: `checkPermissions()` - Update permission status
- **Usage Monitoring**: `startRealTimeMonitoring()` - Background data collection
- **Protection Control**: `startProtection()` / `stopProtection()` - Toggle monitoring
- **Error Handling**: `dismissError()` - Clear error states

### 5. Data Flow Lifecycle

#### Session Tracking

**Session Lifecycle:**

```
App Switch Detected → Session Start → Scroll Tracking → Session Evaluation → Intervention/Continue
```

**Session Data Collection:**

- Package name tracking
- Scroll event counting
- Velocity calculation
- Pause detection
- Duration measurement

#### Intervention Logic

**Evaluation Criteria:**

- Duration threshold (15+ minutes)
- High scroll velocity with low pauses
- Late-night usage sensitivity
- Continuous scrolling patterns

**Intervention Flow:**

```
Session Evaluation → Rule Matching → Intervention Trigger → Notification Display → User Action
```

## Background Processing

### Coroutine Scopes

- **Accessibility Service Scope**: `Dispatchers.Default + SupervisorJob()`
- **Monitoring Service Scope**: `Dispatchers.Default + SupervisorJob()`
- **ViewModel Scope**: `GlobalScope` for long-running operations

### Real-time Monitoring

- **Scroll Events**: Processed immediately through accessibility service
- **Usage Statistics**: Updated every 10 seconds during active monitoring
- **Intervention Checks**: Performed every 5 seconds in monitoring service

## Error Handling & Recovery

### Permission Errors

- Graceful fallback when permissions are denied
- Clear user guidance for permission granting
- Retry mechanisms for permission checks

### Service Errors

- Automatic retry with exponential backoff
- Error state propagation to UI
- Service restart capabilities

### Data Collection Errors

- Fallback to cached data when available
- Error logging for debugging
- User notification of data issues

## Memory Management

### Resource Cleanup

- **Service Destruction**: Cancel coroutines, release resources
- **ViewModel Cleanup**: Clear state flows, stop monitoring
- **Activity Destruction**: Proper cleanup in `onDestroy()`

### Performance Optimization

- Efficient event filtering in accessibility service
- Debounced usage statistics updates
- Minimal memory footprint for background services

## Platform-Specific Considerations

### Android

- **Foreground Service Requirements**: Persistent notification for monitoring service
- **Permission Model**: Runtime permission requests with proper explanations
- **Battery Optimization**: Service persistence despite battery saver modes

### iOS (Future Implementation)

- **Background App Refresh**: Limited background processing capabilities
- **Screen Time API**: Potential integration with native iOS usage tracking
- **Notification Handling**: iOS-specific intervention delivery

## Testing Lifecycle

### Unit Testing

- ViewModel state transitions
- Permission manager logic
- Repository implementations

### Integration Testing

- Service lifecycle management
- Permission flow testing
- Data collection accuracy

### UI Testing

- Compose UI state management
- User interaction flows
- Error state handling

## Best Practices

### Lifecycle Awareness

- Proper cancellation of coroutines
- Resource cleanup in lifecycle methods
- State preservation across configuration changes

### Performance

- Minimal battery impact
- Efficient background processing
- Optimized UI updates

### User Experience

- Clear permission explanations
- Intuitive status indicators
- Meaningful intervention messages

## Troubleshooting

### Common Issues

1. **Permission Denied**: Guide users to settings for manual permission granting
2. **Service Killed**: Implement service restart mechanisms
3. **Data Not Updating**: Check accessibility service status
4. **High Battery Usage**: Optimize monitoring frequency

### Debug Information

- Comprehensive logging throughout lifecycle
- State tracking for debugging
- Error reporting mechanisms

This lifecycle documentation provides a comprehensive overview of how ScrollGuard manages its application lifecycle, from startup to shutdown, including all background services, UI states, and data flows that enable effective social media usage monitoring and intervention.
