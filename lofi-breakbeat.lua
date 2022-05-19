-- noisy breakbeats
-- turn any knob

local lattice=require("lattice")
engine.name="LofiBreakbeat"

local s={
  bpm=120,
  beats=4,
  bpm_clock=120,
  beat=1,
  playing=false,
}

function get_bpm(fname)
  local audio_length=audio.length(fname) -- TODO: correctly get audio lenght
  bpm=fname:match("bpm%d+")
  if bpm~=nil then
    bpm=tonumber(bpm:match("%d+"))
  end
  if bpm~=nil then
    do return bpm,util.round(audio_length/(60/bpm)) end
  end



  local closet_bpm={0,100000}
  for bpm=100,200 do
    local measures=audio_length/((60/bpm)*4)
    if math.round(measures)%2==0 then
      local dif=math.abs(math.round(measures)-measures)
      dif=dif-math.round(measures)/60
      -- print(bpm,math.round(measures),measures,dif)
      if dif<closet_bpm[2] then
        closet_bpm[2]=dif
        closet_bpm[1]=bpm
      end
    end
  end
  return closet_bpm[1],util.round(audio_length/(60/bpm))

end


function init()
  print("init")
  params:add{type="control",id="amp",name="amp",controlspec=controlspec.new(0,0.5,'lin',0,0,'amp',0.01/0.5),action=function(v)
    engine.bb_amp(v)
  end
}
engine.bb_load("/home/we/dust/code/gatherum/data/breakbeats_160bpm2_4beats.wav",160)
engine.bb_bpm(clock.get_tempo())

loop=lattice:new()
end

function toggle_start()
  s.playing=not s.playing
  if s.playing then
    engine.bb_amp(params:get("amp"))
    s.beat=s.beats
    loop:hard_restart()
  else
    loop:stop()
    engine.bb_amp(0)
  end
end

function looping()
  s.beat=s.beat+1
  if s.beat>s.beats then
    s.beat=1
    engine.bb_reset()
  end
  if s.bpm_clock~=clock.get_tempo() then
    s.bpm_clock=clock.get_tempo()
    engine.bb_bpm(s.bpm_clock)
  end
end

function load_file(fname)
  s.bpm.s.beats=get_bpm(fname)
  if bpm==nil then
    print('ERROR: could not find bpm for '..fname)
    do return end
  end
  engine.bb_load(fname,bpm)
end

function enc(k,d)
  params:delta("amp",d)
end

function redraw()
  screen.clear()
  screen.move(64,32)
  screen.text_center("<br></br>")
  screen.update()
end
