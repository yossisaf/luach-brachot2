import struct, re, sys, json
d=open('unpacked/classes.dex','rb').read()
def uleb(o):
    r=0;s=0
    while True:
        b=d[o];o+=1;r|=(b&0x7f)<<s;s+=7
        if not b&0x80:break
    return r,o
h=struct.unpack_from('<8sI20sIIIIIIIIIIIIIIIIIIIIIIIIIIII',d,0) if False else None
(ssz,sof)=struct.unpack_from('<II',d,0x38); (tsz,tof)=struct.unpack_from('<II',d,0x40)
(psz,pof)=struct.unpack_from('<II',d,0x48); (fsz,fof)=struct.unpack_from('<II',d,0x50)
(msz,mof)=struct.unpack_from('<II',d,0x58); (csz,cof)=struct.unpack_from('<II',d,0x60)
S=[]
for i in range(ssz):
    so=struct.unpack_from('<I',d,sof+4*i)[0]
    _,o=uleb(so); e=d.index(b'\0',o); S.append(d[o:e].decode('utf8','replace'))
T=[S[struct.unpack_from('<I',d,tof+4*i)[0]] for i in range(tsz)]
M=[]
for i in range(msz):
    c,p,n=struct.unpack_from('<HHI',d,mof+8*i); M.append((T[c],S[n]))
C=[]
for i in range(csz):
    ci=struct.unpack_from('<I',d,cof+32*i)[0]; C.append(T[ci])
print('strings',ssz,'types',tsz,'methods',msz,'classes',csz,'dex size',len(d))
json.dump({'S':S,'T':T,'M':M,'C':C},open('dex.json','w'))
