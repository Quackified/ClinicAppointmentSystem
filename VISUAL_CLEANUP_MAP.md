# Visual Cleanup Map

## 📦 Files to DELETE (Complete Removal)

```
┌─────────────────────────────────────────────────────────────┐
│                   FILES TO DELETE                            │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ❌ gui/MainWindow.java                                     │
│     │                                                        │
│     ├─ Size: 56 lines                                       │
│     ├─ Type: Old GUI entry point (unused)                   │
│     ├─ References: 0                                         │
│     └─ Status: DEAD CODE - Never called                     │
│                                                              │
│  ❌ gui/components/PaginationPanel.java                     │
│     │                                                        │
│     ├─ Size: 172 lines                                      │
│     ├─ Type: UI Component                                   │
│     ├─ References: 0                                         │
│     └─ Status: Never imported or instantiated               │
│                                                              │
│  ❌ gui/WalkInQueuePanel.java                               │
│     │                                                        │
│     ├─ Size: 473 lines                                      │
│     ├─ Type: Feature UI Panel                               │
│     ├─ References: 1 (MainFrame.java - will be removed)     │
│     └─ Status: FEATURE REMOVAL - Per requirements           │
│                                                              │
└─────────────────────────────────────────────────────────────┘

Total: 3 files, ~701 lines
```

---

## ✂️ Walk-in Queue Feature Removal Map

```
┌────────────────────────────────────────────────────────────────────────┐
│                        WALK-IN QUEUE REMOVAL                            │
├────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  📄 gui/SidebarPanel.java                                              │
│     └─ Line 94: ❌ Remove menu item                                    │
│        addMenuItem("Walk-in Queue", "walkinqueue", ...)                │
│                                                                         │
│  📄 gui/MainFrame.java                                                 │
│     ├─ Line 27: ❌ Remove field declaration                            │
│     │  private WalkInQueuePanel walkInQueuePanel;                      │
│     │                                                                   │
│     ├─ Line 78: ❌ Remove instantiation                                │
│     │  walkInQueuePanel = new WalkInQueuePanel(...);                   │
│     │                                                                   │
│     └─ Line 91: ❌ Remove CardLayout registration                      │
│        contentPanel.add(walkInQueuePanel, "walkinqueue");              │
│                                                                         │
│  📄 service/AppointmentManager.java                                    │
│     │                                                                   │
│     ├─ Data Structures (Lines 26, 29):                                 │
│     │  ❌ private final Queue<Appointment> walkInQueue;                │
│     │  ❌ private final Set<Integer> walkInAppointmentIds;             │
│     │                                                                   │
│     ├─ Constructor (Lines 57-58):                                      │
│     │  ❌ this.walkInQueue = new LinkedList<>();                       │
│     │  ❌ this.walkInAppointmentIds = new HashSet<>();                 │
│     │                                                                   │
│     ├─ Walk-in Methods Block (Lines 524-576): ❌ DELETE ALL            │
│     │  • addToWalkInQueue()                                            │
│     │  • processNextWalkIn()                                           │
│     │  • getWalkInQueueSize()                                          │
│     │  • viewWalkInQueue()                                             │
│     │  • removeFromWalkInQueue()                                       │
│     │  • isWalkInAppointment()                                         │
│     │  • getRegularAppointments()                                      │
│     │                                                                   │
│     ├─ Undo Cleanup:                                                   │
│     │  ❌ Line 379: walkInQueue.remove(appointment);                   │
│     │  ❌ Line 499: walkInQueue.remove(appointment);                   │
│     │                                                                   │
│     └─ Method Parameter (Lines 80-118): ⚠️ SIMPLIFY                   │
│        Remove "boolean isWalkIn" parameter & logic                     │
│                                                                         │
└────────────────────────────────────────────────────────────────────────┘

Total: 3 files modified, ~140 lines removed
```

---

## 🗑️ Optional: Unused Service Methods

```
┌────────────────────────────────────────────────────────────────────────┐
│              OPTIONAL: UNUSED METHODS IN AppointmentManager            │
├────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  Regular Queue Methods (Never Used):                                   │
│  ├─ processNextInQueue()      [Lines 341-353]  ❌                      │
│  ├─ getQueueSize()             [Lines 355-358]  ❌                      │
│  └─ viewQueue()                [Lines 360-363]  ❌                      │
│                                                                         │
│  Undo/Utility Methods (Never Called):                                  │
│  ├─ canUndo()                  [Lines 422-425]  ❌                      │
│  ├─ getUndoStackSize()         [Lines 428-430]  ❌                      │
│  ├─ getAppointmentCount()      [Lines 511-514]  ❌                      │
│  └─ getTodayAppointmentCount() [Lines 516-522]  ❌                      │
│                                                                         │
│  Note: These are OPTIONAL removals. Consider future plans.             │
│                                                                         │
└────────────────────────────────────────────────────────────────────────┘

Total: 1 file, ~60 lines (optional)
```

---

## 📊 Component Status Matrix

```
┌────────────────────────────────────────────────────────────┐
│         COMPONENT                │  STATUS  │   ACTION     │
├──────────────────────────────────┼──────────┼──────────────┤
│ DatePicker.java                  │ ✅ USED  │ KEEP         │
│ TimePicker.java                  │ ✅ USED  │ KEEP         │
│ PaginationPanel.java             │ ❌ UNUSED│ DELETE       │
│ StyledButton.java                │ ✅ USED  │ KEEP         │
│ StyledLabel.java                 │ ✅ USED  │ KEEP         │
│ StyledTextField.java             │ ✅ USED  │ KEEP         │
│ StyledPanel.java                 │ ✅ USED  │ KEEP         │
│ MainWindow.java                  │ ❌ UNUSED│ DELETE       │
│ MainFrame.java                   │ ✅ USED  │ MODIFY       │
│ WalkInQueuePanel.java            │ ⚠️ USED  │ DELETE       │
│ SidebarPanel.java                │ ✅ USED  │ MODIFY       │
│ DashboardPanel.java              │ ✅ USED  │ KEEP         │
│ PatientPanel.java                │ ✅ USED  │ KEEP         │
│ DoctorPanel.java                 │ ✅ USED  │ KEEP         │
│ AppointmentPanel.java            │ ✅ USED  │ KEEP         │
│ SchedulePanel.java               │ ✅ USED  │ KEEP         │
└────────────────────────────────────────────────────────────┘
```

---

## 🎯 Dependency Impact Analysis

```
BEFORE CLEANUP:
═══════════════

ClinicManagementGUI.java (main)
    ├── LoginFrame.java
    └── MainFrame.java
            ├── SidebarPanel.java
            │   └── MenuItem: "Walk-in Queue" ❌
            │
            ├── DashboardPanel.java ✅
            ├── PatientPanel.java ✅
            ├── DoctorPanel.java ✅
            ├── AppointmentPanel.java ✅
            ├── SchedulePanel.java ✅
            └── WalkInQueuePanel.java ❌ REMOVE
                    └── AppointmentManager (walk-in methods) ❌


MainWindow.java (unused main) ❌ DEAD CODE
    └── (never called)


AFTER CLEANUP:
══════════════

ClinicManagementGUI.java (main)
    ├── LoginFrame.java
    └── MainFrame.java
            ├── SidebarPanel.java
            │   └── (Walk-in menu removed) ✅
            │
            ├── DashboardPanel.java ✅
            ├── PatientPanel.java ✅
            ├── DoctorPanel.java ✅
            ├── AppointmentPanel.java ✅
            └── SchedulePanel.java ✅

(MainWindow.java deleted) ✅
(WalkInQueuePanel.java deleted) ✅
(PaginationPanel.java deleted) ✅
```

---

## 📈 Impact Metrics

```
┌──────────────────────────────────────────────────────────────┐
│                      CLEANUP IMPACT                          │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  Files Deleted:           3 files                            │
│  Files Modified:          3 files                            │
│  Lines Removed:           ~760 lines                         │
│                                                              │
│  Code Reduction:          9.5%                               │
│  Dead Code Removed:       100%                               │
│  Feature Removed:         Walk-in Queue                      │
│                                                              │
│  Breaking Changes:        1 (Walk-in queue - intentional)   │
│  Risk Level:              LOW                                │
│  Testing Required:        MEDIUM                             │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

---

## 🧪 Testing Checklist

```
PRE-CLEANUP VERIFICATION:
□ Document current walk-in queue behavior
□ Take screenshots of walk-in queue panel
□ Note any walk-in appointments in demo data

DELETE FILES:
□ Delete gui/MainWindow.java
□ Delete gui/components/PaginationPanel.java
□ Delete gui/WalkInQueuePanel.java
□ Verify compilation (may fail - expected)

MODIFY: gui/SidebarPanel.java
□ Remove line 94 (walk-in menu item)
□ Save and compile

MODIFY: gui/MainFrame.java
□ Remove line 27 (field)
□ Remove line 78 (instantiation)
□ Remove line 91 (CardLayout add)
□ Save and compile

MODIFY: service/AppointmentManager.java
□ Remove walk-in queue fields
□ Remove constructor initialization
□ Remove walk-in methods block
□ Remove undo cleanup lines
□ Simplify scheduleAppointment method
□ Save and compile

TESTING:
□ Application starts without errors
□ Login works
□ Dashboard loads
□ Navigate all menu items (no walk-in option)
□ Create appointment
□ View schedule
□ Patient management works
□ Doctor management works
□ No exceptions in logs

OPTIONAL CLEANUP:
□ Review unused methods
□ Consider removing if no future plans
□ Test again if removed
```

---

## 🚦 Risk Assessment

```
┌─────────────────────────────────────────────────────────┐
│  COMPONENT           │ RISK  │ REASON                   │
├──────────────────────┼───────┼──────────────────────────┤
│ MainWindow           │ 🟢 LOW│ Never referenced         │
│ PaginationPanel      │ 🟢 LOW│ Never referenced         │
│ WalkInQueuePanel     │ 🟢 LOW│ Feature removal          │
│ Walk-in queue logic  │ 🟢 LOW│ Isolated feature         │
│ Undo methods         │ 🟡 MED│ May be future feature    │
│ Regular queue        │ 🟡 MED│ Half-implemented         │
└─────────────────────────────────────────────────────────┘

Overall Risk: 🟢 LOW (Safe to proceed)
```

---

## ✅ Completion Criteria

```
DEFINITION OF DONE:
□ All 3 files deleted
□ All walk-in references removed
□ No compilation errors
□ Application starts successfully
□ All navigation works
□ All features (except walk-in) work
□ No walk-in queue menu item visible
□ Code review approved
□ Tests pass
```

---

**Visual Map Version:** 1.0  
**Last Updated:** 2024-11-26  
**Status:** Ready for implementation
