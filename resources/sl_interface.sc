// code to interact with Sooperlooper

// *******
// setup and configuration
// *******
MIDIClient.init;
~engine = NetAddr.new("localhost", 9951);
SystemClock.sched(2.0, {

8.do({~engine.sendMsg("/loop_add", 1,1)});

// Global settings
~engine.sendMsg("/set", "sync_source", -1);
~engine.sendMsg("/set", "smart_eighths", 0);
~engine.sendMsg("/set", "jack_timebase_master", 1);


~engine.sendMsg("/add_midi_binding", "0 on 55 set tap_tempo -2 0 1 norm 0 127");

~engine_midi = MIDIOut.findPort("sooperlooper-sooperlooper", "sooperlooper-sooperlooper");
MIDIOut.connect(0,~engine_midi);

for (0,7, {arg i; ~engine.sendMsg(format("/sl/%/set", i), "sync", 1)});
for (0,7, {arg i; ~engine.sendMsg(format("/sl/%/set", i), "playback_sync", 1)});
for (0,7, {arg i; ~engine.sendMsg(format("/sl/%/set", i), "tempo_stretch", 1)});
for (0,7, {arg i; ~engine.sendMsg(format("/sl/%/set", i), "relative_sync", 1)});
for (0,7, {arg i; ~engine.sendMsg(format("/sl/%/set", i), "quantize", 2)});
for (0,7, {arg i; ~engine.sendMsg(format("/sl/%/set", i), "mute_quantized", 1)});
for (0,7, {arg i; ~engine.sendMsg(format("/sl/%/set", i), "overdub_quantized", 1)});

nil;
});


~player = Routine({
MIDIOut(0).noteOn(0,55);
});


//OSC control

// MIDI control

// loop soundfile access


// maybe this will be 