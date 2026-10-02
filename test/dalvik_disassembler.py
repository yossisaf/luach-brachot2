import struct,sys,re,json,os
d=open('unpacked/classes.dex','rb').read()
U=lambda o:struct.unpack_from('<I',d,o)[0]; H=lambda o:struct.unpack_from('<H',d,o)[0]
def uleb(o):
    r=0;s=0
    while True:
        b=d[o];o+=1;r|=(b&0x7f)<<s;s+=7
        if not b&0x80:break
    return r,o
ssz,sof=U(0x38),U(0x3c);tsz,tof=U(0x40),U(0x44);psz,pof=U(0x48),U(0x4c);fsz,fof=U(0x50),U(0x54);msz,mof=U(0x58),U(0x5c);csz,cof=U(0x60),U(0x64)
S=[]
for i in range(ssz):
    _,o=uleb(U(sof+4*i)); e=d.index(b'\0',o)
    S.append(d[o:e].decode('utf8','replace'))
T=[S[U(tof+4*i)] for i in range(tsz)]
def tlist(off):
    if not off: return []
    n=U(off); return [T[H(off+4+2*i)] for i in range(n)]
P=[]
for i in range(psz):
    sh,rt,po=U(pof+12*i),U(pof+12*i+4),U(pof+12*i+8)
    P.append('('+''.join(tlist(po))+')'+T[rt])
F=[(T[H(fof+8*i)],S[U(fof+8*i+4)],T[H(fof+8*i+2)]) for i in range(fsz)]
M=[(T[H(mof+8*i)],S[U(mof+8*i+4)],P[H(mof+8*i+2)]) for i in range(msz)]
ops={}
def reg(s):
    for line in s.strip().split('\n'):
        a=line.split()
        ops[int(a[0],16)]=(a[1],a[2])
reg('''00 nop 10x
01 move 12x
02 move/from16 22x
03 move/16 32x
04 move-wide 12x
05 move-wide/from16 22x
06 move-wide/16 32x
07 move-object 12x
08 move-object/from16 22x
09 move-object/16 32x
0a move-result 11x
0b move-result-wide 11x
0c move-result-object 11x
0d move-exception 11x
0e return-void 10x
0f return 11x
10 return-wide 11x
11 return-object 11x
12 const/4 11n
13 const/16 21s
14 const 31i
15 const/high16 21h
16 const-wide/16 21s
17 const-wide/32 31i
18 const-wide 51l
19 const-wide/high16 21h
1a const-string 21c:s
1b const-string/jumbo 31c:s
1c const-class 21c:t
1d monitor-enter 11x
1e monitor-exit 11x
1f check-cast 21c:t
20 instance-of 22c:t
21 array-length 12x
22 new-instance 21c:t
23 new-array 22c:t
24 filled-new-array 35c:t
25 filled-new-array/range 3rc:t
26 fill-array-data 31t
27 throw 11x
28 goto 10t
29 goto/16 20t
2a goto/32 30t
2b packed-switch 31t
2c sparse-switch 31t
2d cmpl-float 23x
2e cmpg-float 23x
2f cmpl-double 23x
30 cmpg-double 23x
31 cmp-long 23x
32 if-eq 22t
33 if-ne 22t
34 if-lt 22t
35 if-ge 22t
36 if-gt 22t
37 if-le 22t
38 if-eqz 21t
39 if-nez 21t
3a if-ltz 21t
3b if-gez 21t
3c if-gtz 21t
3d if-lez 21t''')
names='aget aget-wide aget-object aget-boolean aget-byte aget-char aget-short aput aput-wide aput-object aput-boolean aput-byte aput-char aput-short'.split()
for i,n in enumerate(names): ops[0x44+i]=(n,'23x')
sfx=['','-wide','-object','-boolean','-byte','-char','-short']
for i,s in enumerate(sfx): ops[0x52+i]=('iget'+s,'22c:f'); ops[0x59+i]=('iput'+s,'22c:f'); ops[0x60+i]=('sget'+s,'21c:f'); ops[0x67+i]=('sput'+s,'21c:f')
for i,n in enumerate(['virtual','super','direct','static','interface']):
    ops[0x6e+i]=('invoke-'+n,'35c:m'); ops[0x74+i]=('invoke-'+n+'/range','3rc:m')
un='neg-int not-int neg-long not-long neg-float neg-double int-to-long int-to-float int-to-double long-to-int long-to-float long-to-double float-to-int float-to-long float-to-double double-to-int double-to-long double-to-float int-to-byte int-to-char int-to-short'.split()
for i,n in enumerate(un): ops[0x7b+i]=(n,'12x')
bi='add sub mul div rem and or xor shl shr ushr'.split()
for i,n in enumerate(bi): ops[0x90+i]=(n+'-int','23x'); ops[0xb0+i]=(n+'-int/2addr','12x')
for i,n in enumerate(bi): ops[0x9b+i]=(n+'-long','23x'); ops[0xbb+i]=(n+'-long/2addr','12x')
for i,n in enumerate(bi[:5]): ops[0xa6+i]=(n+'-float','23x'); ops[0xc6+i]=(n+'-float/2addr','12x'); ops[0xab+i]=(n+'-double','23x'); ops[0xcb+i]=(n+'-double/2addr','12x')
for i,n in enumerate(['add','rsub','mul','div','rem','and','or','xor']): ops[0xd0+i]=(n+'-int/lit16','22s'); ops[0xd8+i]=(n+'-int/lit8','22b')
for i,n in enumerate(['shl','shr','ushr']): ops[0xe0+i]=(n+'-int/lit8','22b')
ops[0xfa]=('invoke-polymorphic','45cc');ops[0xfb]=('invoke-polymorphic/range','4rcc');ops[0xfc]=('invoke-custom','35c:x');ops[0xfd]=('invoke-custom/range','3rc:x');ops[0xfe]=('const-method-handle','21c:x');ops[0xff]=('const-method-type','21c:x')
SZ={'10x':1,'12x':1,'11n':1,'11x':1,'10t':1,'20t':2,'22x':2,'21t':2,'21s':2,'21h':2,'21c':2,'23x':2,'22b':2,'22t':2,'22s':2,'22c':2,'30t':3,'32x':3,'31i':3,'31t':3,'31c':3,'35c':3,'3rc':3,'51l':5,'45cc':4,'4rcc':4}
def s32(x): return x-(1<<32) if x>=1<<31 else x
def s16(x): return x-(1<<16) if x>=1<<15 else x
def s8(x): return x-256 if x>=128 else x
def s4(x): return x-16 if x>=8 else x
def ref(k,i):
    if k=='s': return json.dumps(S[i],ensure_ascii=False)
    if k=='t': return T[i]
    if k=='f': c,n,t=F[i]; return '%s->%s:%s'%(c,n,t)
    if k=='m': c,n,p=M[i]; return '%s->%s%s'%(c,n,p)
    return '#%d'%i
def disasm(off):
    regs,ins,outs,tries,dbg,n=struct.unpack_from('<HHHHII',d,off)
    base=off+16; u=lambda i:H(base+2*i)
    out=[]; i=0
    while i<n:
        w=u(i); op=w&0xff
        if w in (0x0100,0x0200,0x0300) and op==0:  # payload
            if w==0x0100:
                sz=u(i+1); ln=4+sz*2; out.append((i,'.packed-switch-data (%d)'%sz)); i+=ln; continue
            if w==0x0200:
                sz=u(i+1); ln=2+sz*4; out.append((i,'.sparse-switch-data (%d)'%sz)); i+=ln; continue
            if w==0x0300:
                ew=u(i+1); sz=u(i+2)|(u(i+3)<<16); ln=4+(ew*sz+1)//2; out.append((i,'.array-data')); i+=ln; continue
        name,fmt=ops.get(op,('unused-%02x'%op,'10x')); k=None
        if ':' in fmt: fmt,k=fmt.split(':')
        a=w>>8; txt=name
        if fmt=='10x': pass
        elif fmt=='12x': txt+=' v%d, v%d'%(a&15,a>>4)
        elif fmt=='11n': txt+=' v%d, %d'%(a&15,s4(a>>4))
        elif fmt=='11x': txt+=' v%d'%a
        elif fmt=='10t': txt+=' :%04x'%(i+s8(a))
        elif fmt=='20t': txt+=' :%04x'%(i+s16(u(i+1)))
        elif fmt=='22x': txt+=' v%d, v%d'%(a,u(i+1))
        elif fmt=='21t': txt+=' v%d, :%04x'%(a,i+s16(u(i+1)))
        elif fmt=='21s': txt+=' v%d, %d'%(a,s16(u(i+1)))
        elif fmt=='21h': txt+=' v%d, 0x%x0000'%(a,u(i+1)) if op==0x15 else ' v%d, 0x%x000000000000'%(a,u(i+1))
        elif fmt=='21c': txt+=' v%d, %s'%(a,ref(k,u(i+1)))
        elif fmt=='23x': b=u(i+1); txt+=' v%d, v%d, v%d'%(a,b&255,b>>8)
        elif fmt=='22b': b=u(i+1); txt+=' v%d, v%d, %d'%(a,b&255,s8(b>>8))
        elif fmt=='22t': txt+=' v%d, v%d, :%04x'%(a&15,a>>4,i+s16(u(i+1)))
        elif fmt=='22s': txt+=' v%d, v%d, %d'%(a&15,a>>4,s16(u(i+1)))
        elif fmt=='22c': txt+=' v%d, v%d, %s'%(a&15,a>>4,ref(k,u(i+1)))
        elif fmt=='30t': txt+=' :%04x'%(i+s32(u(i+1)|(u(i+2)<<16)))
        elif fmt=='32x': txt+=' v%d, v%d'%(u(i+1),u(i+2))
        elif fmt=='31i': txt+=' v%d, %d'%(a,s32(u(i+1)|(u(i+2)<<16)))
        elif fmt=='31t': txt+=' v%d, :%04x'%(a,i+s32(u(i+1)|(u(i+2)<<16)))
        elif fmt=='31c': txt+=' v%d, %s'%(a,ref(k,u(i+1)|(u(i+2)<<16)))
        elif fmt=='35c':
            cnt=a>>4; g=a&15; idx=u(i+1); r=u(i+2)
            rs=[r&15,(r>>4)&15,(r>>8)&15,(r>>12)&15,g][:cnt]
            txt+=' {%s}, %s'%(', '.join('v%d'%x for x in rs),ref(k,idx))
        elif fmt=='3rc':
            idx=u(i+1); r=u(i+2); txt+=' {v%d .. v%d}, %s'%(r,r+a-1,ref(k,idx))
        elif fmt=='51l':
            v=u(i+1)|(u(i+2)<<16)|(u(i+3)<<32)|(u(i+4)<<48); txt+=' v%d, %d'%(a,v)
        out.append((i,txt)); i+=SZ[fmt]
    return regs,ins,outs,out
classes=[]
for ci in range(csz):
    o=cof+32*ci; cidx,acc,sup,ifs,src,ann,cd,sv=struct.unpack_from('<8I',d,o)
    ms=[]
    if cd:
        p=cd; sf,p=uleb(p); inf,p=uleb(p); dm,p=uleb(p); vm,p=uleb(p)
        fl=[]
        for cnt in (sf,inf):
            idx=0
            for _ in range(cnt):
                df,p=uleb(p); a,p=uleb(p); idx+=df; fl.append((idx,a))
        for kind,cnt in (('direct',dm),('virtual',vm)):
            idx=0
            for _ in range(cnt):
                df,p=uleb(p); a,p=uleb(p); co,p=uleb(p); idx+=df; ms.append((kind,idx,a,co))
    classes.append((T[cidx],T[sup] if sup!=0xffffffff else None,S[src] if src!=0xffffffff else None,ms))
if __name__=='__main__':
    # find classes whose code contains interesting const-strings
    key=re.compile(r'[\u0590-\u05FF]|SharedPref|getSharedPreferences|FoodItem|MainAppScreen|hl5047|mitmachim')
    hits={}
    for cn,sup,src,ms in classes:
        if cn.startswith(('Landroidx','Lkotlin','Landroid/','Ljava')): continue
        for kind,mi,acc,co in ms:
            if not co: continue
            regs,ins,outs,out=disasm(co)
            for off,t in out:
                if t.startswith('const-string') and key.search(t): hits.setdefault(cn,set()).add(M[mi][1])
    print(len(hits)); 
    for c,v in sorted(hits.items()): print(c,sorted(v)[:6])
