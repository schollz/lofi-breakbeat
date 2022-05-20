-- noisy breakbeats
-- turn any knob

local lattice_=require("lattice")
engine.name="LofiBreakbeat"

local s={
  shift=false,
  bpm=120,
  beats=4,
  bpm_clock=120,
  beat=1,
  playing=false,
}

function get_bpm(fname)
local ch,samples,samplerate=audio.file_info(fname)
  local audio_length=samples/48000.0
  print(fname,audio_length)
  local bpm=fname:match("bpm%d+")
  if bpm~=nil then
    bpm=tonumber(bpm:match("%d+"))
  end
  if bpm~=nil then
    print(bpm,util.round(audio_length/(60/bpm)))
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
  return closet_bpm[1],util.round(audio_length/(60/closet_bpm[1]))
end


function init()
  print("init")
  params:add{type="control",id="amp",name="amp",controlspec=controlspec.new(0,0.5,'lin',0,0.5,'amp',0.01/0.5),action=function(v)
    engine.bb_amp(v)
  end
}
params:add_file("bb_file","load file",_path.audio)
params:set_action("bb_file",function(fname) load_file(fname) end)
params:add{type="binary",name="play",id="bb_play",behavior="toggle",action=function(v)
  toggle_start(v==1)
end}
load_file("/home/we/dust/code/lofi-breakbeat/lib/loop_bpm150.wav")
lattice=lattice_:new()
pattern=lattice:new_pattern{
  action=function(t)
    looping()
  end,
  division=1/4,
}
toggle_start(true)
end

function toggle_start(go)
  if go~=nil then
    s.playing=go
  else
    s.playing=not s.playing
  end
  if s.playing then
    engine.bb_amp(params:get("amp"))
    s.beat=s.beats
    lattice:hard_restart()
  else
    lattice:stop()
    engine.bb_amp(0)
  end
end

function looping()
  s.beat=s.beat+1
  if s.beat>s.beats then
    s.beat=1
    engine.bb_jump()
  end
  if s.bpm_clock~=clock.get_tempo() then
    s.bpm_clock=clock.get_tempo()
    engine.bb_bpm(s.bpm_clock)
  end
end

function load_file(fname)
  s.bpm,s.beats=get_bpm(fname)
  if s.bpm==nil then
    print('ERROR: could not find bpm for '..fname)
    do return end
  end
  engine.bb_load(fname,s.bpm)
end

function enc(k,d)
  params:delta("amp",d)
end

function key(k,z)
  if k==1 then
    s.shift=z==1
  elseif k==2 and z==1 then
    if s.shift then
      engine.bb_rate()
    else
      engine.bb_capture()
    end
  elseif k==3 and z==1 then
    if s.shift then
      toggle_start()
    else
      engine.bb_jump()
    end
  end
end

function redraw()
  screen.clear()
  screen.move(64,32)
  screen.text_center("<br></br>")
  screen.update()
end
