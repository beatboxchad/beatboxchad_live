// Synths intended for monitoring a HW input for pitch, amplitude, etc for output to a control bus. later, bbcut and other fanciness


// clojure code will set up this phase of the studio
(
//pitch
SynthDef("pitch1", {

}).load();

SynthDef("amplitude1", {

}).load();



(
SynthDef("percusReson1", {
	//TODO Ugen or function that changes a note to a delay time for the oscillating delays

	arg controlbus1, in, out = [0, 1];
	var delaytime = In.kr(controlbus1).linlin(1, 127, 0.01, 0.3), delays, signal;
	signal = Mix.new([SoundIn.ar(in)]);

	delays = Mix.fill(100, {arg i; var dt;
		dt = delaytime*(i/10+0.1);
		DelayL.ar(signal, 1.2, dt);});

	Out.ar(out, (signal+delays));
}).load()

)

(
SynthDef("pitchFollow1",{
    arg outBus, inBus;
	var in, amp, freq, hasFreq, out;
    in = Mix.new(SoundIn.ar(inBus));
    amp = Amplitude.kr(in, 0.05, 0.05);
    # freq, hasFreq = Pitch.kr(in, ampThreshold: 0.02, median: 7);
    //freq = Lag.kr(freq.cpsmidi.round(1).midicps, 0.05);
    out = Mix.new(VarSaw.ar(freq * [0.5,1,2], 0, LFNoise1.kr(0.3,0.1,0.1), amp));

    Out.ar(outbus, out)
}).load();
)

(
SynthDef("funDelay1", {
	arg in, out, feedbackBus, dtimeBus;
	var maxdelay, dtime, decay, sig;
	sig = SoundIn.ar(in);
	dtime = In.kr(dtimeBus).linlin(1, 127, 0.2, 4);
	decay = In.kr(feedbackBus).linlin(1,127, 1, 24) * dtime;
	maxdelay = 24 * dtime;
	sig = Mix.new([sig, CombC.ar(sig, maxdelay, dtime, decay)]);
	Out.ar(out, sig);
}).load();
)


(
SynthDef("reverb1", {
	arg inBus, outBus;
	var sig, in;
	in = SoundIn.ar(inBus);
	sig = FreeVerb.ar(in);
	Out.ar(outBus, sig + in);
}).load()
)


(
SynthDef("vocoder1", {
	arg modulator, carrier, out;
	var sig;
	// guitar signals have to be amplified for it to sound good.
	sig = Vocode.ar(SoundIn.ar(carrier), SoundIn.ar(modulator), 56);
	Out.ar(out, sig);
}).load()
)

