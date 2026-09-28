# ExperienceLab — Enhancement Plan (not yet implemented)

A running list of ideas parked for later. Nothing here is built yet.

## 1. Scene "Auto Tour" (auto-rotate 360°)

### Origin
Discovered by accident: while inside the **Bedroom** scene, the 360° view began
panning on its own without any head movement.

### Root cause (diagnosed from logs)
It is **not** a bug in the scene/music features and it does **not** affect head
tracking. The bundled NRSDK occasionally emits a **mildly biased gyro sample**
that passes the existing ±50 rad/s corrupt-sample filter in `HeadTracker.kt`
(which only rejects the extreme `~1e11` spikes). A small non-zero gyro value at
rest integrates over time into a **slow continuous yaw** — the "spin on its own".

So the accidental effect is real but comes from unreliable sensor drift, which
is random and could also cause unwanted spin / discomfort.

### Proposed feature (safe, decoupled from the drift bug)
Add a **deliberate, controllable Auto Tour**:
- A gentle constant yaw applied in `Mpv360Controller.kt`, **added on top of** the
  head-tracked orientation (never replacing it).
- Head movement still works and steers it; when the user holds still it slowly
  drifts to showcase the scene.
- **Off by default.** Trigger options to decide:
  - a small "Auto Tour" toggle button in the scene, or
  - auto-start after N seconds of no head movement (auto-pause on head motion).

### Companion safe fix (independent)
Tighten drift protection in `HeadTracker.kt` with an **at-rest gyro deadzone /
running-bias correction** so the *unwanted* accidental spin stops. This only
removes idle drift; it does not reduce responsiveness to real head motion.

### Constraints
- Must not break head tracking (hard-won) or the scene background music.
- Both changes are additive and guarded; auto-tour is opt-in.

### Status
Parked — awaiting decision on trigger (button vs. idle auto-start).
