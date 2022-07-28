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
            arg out=0,amp=0,bpmsource=150,bpm=150,t_trig=0,t_reset=0,bufnum,bufnumtemp,lpf=6000,hpf=100,mix=0;
            var playbuf,playbuf2,snd,rate,tempotrigger;
            tempotrigger=Impulse.kr(bpm/60);
            rate = bpm/bpmsource*BufRateScale.kr(bufnum);
            rate = rate*Lag.kr(TChoose.kr(tempotrigger,[1,1,1,1,1,-1]),60/bpm*TChoose.kr(Dust.kr(1),[0,0,0,1,2,4]));
            playbuf2=PlayBuf.ar(2,bufnum,rate,t_reset,0,loop:1);
            playbuf=PlayBuf.ar(2,bufnum,rate,t_trig,TChoose.kr(tempotrigger,(0..16)/16)*BufFrames.kr(bufnum),loop:1);
            snd=Breakcore.ar(bufnumtemp,playbuf,
                capturetrigger:Impulse.kr(bpm/60*TChoose.kr(tempotrigger,0.125*(1..32))),
                duration:TWChoose.kr(tempotrigger,[0.125/4,0.125/2,0.125,0.25,0.5,1,2,4,8],[0.25,0.5,1,1,3,3,3,3,4],1)*48000*60/bpm,
                ampdropout:1
            );
            snd=SelectX.ar(Lag.kr(mix),[playbuf2,snd]);
            snd=HPF.ar(snd,Lag.kr(hpf,3));
            snd=RLPF.ar(snd,Lag.kr(lpf,3),0.707);
            Out.ar(0,snd*Lag.kr(amp))
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
            synLofiBreakbeat.set(\t_trig,1);
        });
        this.addCommand("bb_reset","", { arg msg;
            synLofiBreakbeat.set(\t_reset,1);
        });

        this.addCommand("bb_mix","f", { arg msg;
            synLofiBreakbeat.set(\mix,msg[1]);
        });

        this.addCommand("bb_rate","", { arg msg;
        
        });

        this.addCommand("bb_capture","", { arg msg;

        });

        this.addCommand("bb_bpm","f", { arg msg;
            synLofiBreakbeat.set(\bpm,msg[1]);
        });

        this.addCommand("bb_amp","f", { arg msg;
            synLofiBreakbeat.set(\amp,msg[1]);
        });

        this.addCommand("bb_lpf","f", { arg msg;
            synLofiBreakbeat.set(\lpf,msg[1]);
        });

        this.addCommand("bb_hpf","f", { arg msg;
            synLofiBreakbeat.set(\hpf,msg[1]);
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
