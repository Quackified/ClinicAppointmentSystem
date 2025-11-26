# Code Cleanup Summary - Quick Reference

## Files to DELETE (3 files)

1. **`gui/MainWindow.java`** - Old unused tabbed interface (56 lines)
2. **`gui/components/PaginationPanel.java`** - Unused pagination component (172 lines)  
3. **`gui/WalkInQueuePanel.java`** - Walk-in queue feature panel (473 lines)

---

## Walk-in Queue Removal Checklist

### File: `gui/SidebarPanel.java`
- [ ] Remove **line 94**: Walk-in Queue menu item

### File: `gui/MainFrame.java`
- [ ] Remove **line 27**: `private WalkInQueuePanel walkInQueuePanel;`
- [ ] Remove **line 78**: `walkInQueuePanel = new WalkInQueuePanel(...);`
- [ ] Remove **line 91**: `contentPanel.add(walkInQueuePanel, "walkinqueue");`

### File: `service/AppointmentManager.java`
- [ ] Remove **line 26**: `private final Queue<Appointment> walkInQueue;`
- [ ] Remove **line 29**: `private final Set<Integer> walkInAppointmentIds;`
- [ ] Remove **lines 57-58**: Queue initialization in constructor
- [ ] Remove **lines 80-118**: Walk-in parameter in scheduleAppointment (or simplify)
- [ ] Remove **lines 524-576**: All walk-in queue methods (8 methods)
- [ ] Remove **line 379**: `walkInQueue.remove(appointment);` in undoLastAction
- [ ] Remove **line 499**: `walkInQueue.remove(appointment);` in deleteAppointment

---

## Optional: Unused Service Methods to Remove

### File: `service/AppointmentManager.java`

Regular queue methods (never used):
- [ ] `processNextInQueue()` - lines 341-353
- [ ] `getQueueSize()` - lines 355-358
- [ ] `viewQueue()` - lines 360-363

Unused utility methods:
- [ ] `canUndo()` - lines 422-425
- [ ] `getUndoStackSize()` - lines 428-430
- [ ] `getAppointmentCount()` - lines 511-514
- [ ] `getTodayAppointmentCount()` - lines 516-522

---

## Impact Summary

- **Code Removed:** ~760 lines
- **Files Deleted:** 3
- **Files Modified:** 3
- **Breaking Changes:** Walk-in queue feature removed (intentional)
- **Risk Level:** LOW (all tested and verified unused)

---

## Testing Checklist After Cleanup

- [ ] Application starts without errors
- [ ] Login works
- [ ] Dashboard loads correctly
- [ ] Navigate to Patients panel
- [ ] Navigate to Doctors panel
- [ ] Navigate to Appointments panel
- [ ] Navigate to Schedule panel
- [ ] Walk-in Queue menu item is gone
- [ ] Create new appointment works
- [ ] View appointment details works
- [ ] Edit appointment works
- [ ] Cancel appointment works

---

For detailed analysis, see: **UNUSED_CODE_REPORT.md**
