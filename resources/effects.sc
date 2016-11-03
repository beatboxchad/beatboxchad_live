// Synths intended for monitoring a HW input for pitch, amplitude, etc for output to a control bus. later, bbcut and other fanciness

// some day these will all be translated into Overtone.
(

//pitch
SynthDef("pitch1", {
	arg in, out;
	var freq, hasFreq;
	# freq, hasFreq = Pitch.kr(In.ar(in), ampThreshold: 0.02, median: 7);
	Out.kr(out, freq);
}).load();

//amplitude
SynthDef(\amplitude1, {
	arg in, out;
	var amp = Amplitude.kr(In.ar(in), 0.05, 0.05);
	Out.kr(out, amp);
}).load();

//beat slice!!!


// a long list of analyzers could go here.
)

// actual effects
(
SynthDef(\funDelay1, {
	arg in, out, feedbackBus, dtimeBus;
	var maxdelay, dtime, decay, sig;
	sig = In.ar(in);
	dtime = In.kr(dtimeBus).linlin(0, 127, 0.2, 4);
	decay = In.kr(feedbackBus).linlin(0,127, 1, 24) * dtime;
	maxdelay = 24 * dtime;
	sig = Mix.new([sig, CombC.ar(sig, maxdelay, dtime, decay)]);
	Out.ar(out, sig);
}).load();

SynthDef(\funDelay2, {
	//TODO Ugen or function that changes a note to a delay time for the oscillating delays

	arg dtimeCtl, in, out = [0, 1];
	var delaytime = In.kr(controlbus1).linlin(1, 127, 0.01, 0.3), delays, signal;
	signal = Mix.new([SoundIn.ar(in)]);

	delays = Mix.fill(100, {arg i; var dt;
		dt = delaytime*(i/10+0.1);
		DelayL.ar(signal, 1.2, dt);});

	Out.ar(out, (signal+delays));
}).load()

// from ixi tutorial 12 -- you can get some WILD sounds out of this if you turn up the settings
// once again you can get notes out of it by turning up the rate -- TODO WRITE SOMETHING FOR THAT, THAT IS YOUR SOUND
SynthDef(\flanger, {
	arg out=0, in=0, delay=0.1, depth=0.08, rate=0.06, fdbk=0.0, decay=0.0;
	var input, maxdelay, maxrate, dsig, mixed, local;
	maxdelay = 0.013;
	maxrate = 10.0;
	input = In.ar(in, 1);
	local = LocalIn.ar(1);
	dsig = AllpassL.ar( // the delay (you could use AllpassC (put 0 in decay))
		input + (local * fdbk),
		maxdelay * 2,
		LFPar.kr( // very similar to SinOsc (try to replace it) - Even use LFTri
			rate * maxrate,
			0,
			depth * maxdelay,
			delay * maxdelay),
		decay);
	mixed = input + dsig;
	LocalOut.ar(mixed);
	Out.ar(out, mixed);
}).load();

// also from ixi 12, this one's not working as intended but the breakage sounds quite pleasant.
SynthDef(\chorus, { arg inbus=10, outbus=0, predelay=0.08, speed=0.05, depth=0.1, ph_diff=0.5;
	var in, sig, modulators, numDelays = 12;
	in = In.ar(inbus, 1);
	modulators = Array.fill(numDelays, {arg i;
    LFPar.kr(speed * rrand(0.94, 1.06), ph_diff * i, depth, predelay);});
	sig = DelayC.ar(in, 0.5, modulators);
	sig = sig.sum; //Mix(sig);
	Out.ar(outbus, sig!2); // output in stereo

}).load()
)

// synths
