import struct, sys
def parse(data):
    off=8
    strings=[]; out=[]; ns={}
    depth=0
    def rs(o,utf8):
        if utf8:
            l=data[o]; o+=1
            if l&0x80: l=((l&0x7f)<<8)|data[o]; o+=1
            l2=data[o]; o+=1
            if l2&0x80: l2=((l2&0x7f)<<8)|data[o]; o+=1
            return data[o:o+l2].decode('utf8','replace')
        l=struct.unpack_from('<H',data,o)[0]; o+=2
        if l&0x8000: l=((l&0x7fff)<<16)|struct.unpack_from('<H',data,o)[0]; o+=2
        return data[o:o+l*2].decode('utf16','replace')
    while off<len(data):
        t,hs,sz=struct.unpack_from('<HHI',data,off)
        if t==0x1:
            cnt,sc,flags,sstart,_=struct.unpack_from('<IIIII',data,off+8)
            utf8=bool(flags&0x100)
            offs=struct.unpack_from('<%dI'%cnt,data,off+28)
            for o in offs: strings.append(rs(off+sstart+o,utf8))
        elif t==0x102:
            line,_,nsi,name,_,_,ac,_,_,_=struct.unpack_from('<IIIIHHHHHH',data,off+8)
            # ns idx, name idx at off+16.. ; attrStart etc
            ln,cm,nsidx,nameidx,attrstart,attrsize,attrcount=struct.unpack_from('<IIiiHHH',data,off+8)
            attrs=[]
            ao=off+16+attrstart
            for i in range(attrcount):
                ans,an,raw,vs,vres,vt,vd=struct.unpack_from('<iiiHBBI',data,ao+i*20)
                name=strings[an] if an>=0 else ''
                if raw>=0: val=strings[raw]
                elif vt==0x12: val='true' if vd else 'false'
                elif vt==0x10: val=str(struct.unpack('<i',struct.pack('<I',vd))[0])
                elif vt==0x11: val='0x%x'%vd
                elif vt==0x01: val='@0x%x'%vd
                else: val='type%d:0x%x'%(vt,vd)
                attrs.append((name,val))
            out.append('  '*depth+'<'+strings[nameidx]+' '+' '.join('%s="%s"'%a for a in attrs)+'>'); depth+=1
        elif t==0x103:
            depth-=1
            nameidx=struct.unpack_from('<i',data,off+20)[0]
            out.append('  '*depth+'</'+strings[nameidx]+'>')
        off+=sz
    return '\n'.join(out)
print(parse(open(sys.argv[1],'rb').read()))
