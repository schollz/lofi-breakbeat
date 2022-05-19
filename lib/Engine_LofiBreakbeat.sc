// Engine_LofiBreakbeat

// Inherit methods from CroneEngine
Engine_LofiBreakbeat : CroneEngine {

    // LofiBreakbeat specific v0.1.0
    var sampleBuffLofiBreakbeat;
    var breakBuffLofiBreakbeat;
    var synLofiBreakbeat;
    // LofiBreakbeat ^

    *new { arg context, doneCallback;
        ^super.new(context, doneCallback);
    }

    alloc {
        // LofiBreakbeat specific v0.0.1
        breakBuffLofiBreakbeat = Buffer.alloc(context.server,48000*4,2);
        sampleBuffLofiBreakbeat = Buffer.new(context.server);

        SynthDef("SynDefLofiBreakbeat",{
            arg out=0,amp=0,bpmsource=150,bpm=150,t_capture=0,t_jump=0,t_rate,bufnum,bufnumtemp;
            var playbuf,snd,rate;
            rate = bpm/bpmsource*BufRateScale.kr(bufnum);
            rate = rate*Lag.kr(TChoose.kr(t_rate,[1,1,-1]),60/bpm*TChoose.kr(t_rate,[0,0.1,0.5,1,2,4]));
            playbuf=PlayBuf.ar(2,bufnum,rate,t_jump,TChoose.kr(t_jump,(0..16)/16)*BufFrames.kr(bufnum),loop:1);
            snd=LofiBreakbeat.ar(bufnumtemp,playbuf,
                capturetrigger:t_capture*TChoose.kr(t_trig,0.125*(1..32)),
                duration:TWChoose.kr(t_capture,[0.125/4,0.125/2,0.125,0.25,0.5,1,2,4,8],[0.25,0.5,1,1,3,3,3,3,4],1)*48000*60/bpm,
                ampdropout:1
            );
            snd=HPF.ar(snd,100);
            snd=LPF.ar(snd,6000);
            Out.ar(0,snd*Lag.kr(amp,1))
        }).add;

        context.server.sync;

        synLofiBreakbeat = Synth("SynDefLofiBreakbeat",[
            \out,0,
            \bufnumtemp,breakBuffLofiBreakbeat;
        ], context.xg);

        context.server.sync;

        this.addCommand("bb_load","sf", { arg msg;
            sampleBuffLofiBreakbeat.free;
            "loading file".postln;
            sampleBuffLofiBreakbeat = Buffer.read(context.server,msg[1],action:{
                "loaded file".postln;
                synLofiBreakbeat.set(\bufnum,sampleBuffLofiBreakbeat.bufnum,\bpmsource,msg[2]);
            });
        });

        this.addCommand("bb_jump","", { arg msg;
            synLofiBreakbeat.set(\t_jump,1);)
        });

        this.addCommand("bb_rate","", { arg msg;
            synLofiBreakbeat.set(\t_rate,1);)
        });

        this.addCommand("bb_capture","", { arg msg;
            synLofiBreakbeat.set(\t_capture,1);)
        });

        this.addCommand("bb_bpm","f", { arg msg;
            synLofiBreakbeat.set(\bpm,msg[1])
        });

        this.addCommand("bb_amp","f", { arg msg;
            synLofiBreakbeat.set(\amp,msg[1])
        });

        // ^ LofiBreakbeat specific

    }

    free {
        // LofiBreakbeat Specific v0.0.1
        sampleBuffLofiBreakbeat.free;
        breakBuffLofiBreakbeat.free;
        synLofiBreakbeat.free;
        // ^ LofiBreakbeat specific
    }
}
