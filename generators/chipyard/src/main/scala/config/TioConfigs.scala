package chipyard

import org.chipsalliance.cde.config.{Config}
import freechips.rocketchip.diplomacy.{AsynchronousCrossing}

// ------------------------------
// Config for Tio accelerator
// ------------------------------

class TioConfig extends Config(
  new tio.WithTio ++
  new freechips.rocketchip.subsystem.WithNBigCores(1) ++
  new freechips.rocketchip.subsystem.WithoutTLMonitors ++         // Faster without Tilelink monitors
  new chipyard.config.AbstractConfig)

