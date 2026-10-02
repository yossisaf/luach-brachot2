.class Ltz;
.super Ljava/lang/Object;
.source r8-map-id-376676f4a107047edd484746d01b22a49a6869c08ffa71e3048f26960de7e16e

.method direct <init>(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V  #  acc=0x10001
  .registers 4 ins=4
  0000: invoke-virtual {v2}, Ljava/lang/Object;->getClass()Ljava/lang/Class;
  0003: invoke-direct {v0}, Ljava/lang/Object;-><init>()V
  0006: iput-object v1, v0, Ltz;->a:Ljava/lang/String;
  0008: iput-object v2, v0, Ltz;->b:Ljava/lang/String;
  000a: iput-object v3, v0, Ltz;->c:Ljava/lang/String;
  000c: return-void
.end method

.method virtual equals(Ljava/lang/Object;)Z  #  acc=0x11
  .registers 4 ins=2
  0000: if-ne v2, v3, :0003
  0002: goto :002c
  0003: instance-of v0, v3, Ltz;
  0005: if-nez v0, :0008
  0007: goto :002a
  0008: check-cast v3, Ltz;
  000a: iget-object v0, v2, Ltz;->a:Ljava/lang/String;
  000c: iget-object v1, v3, Ltz;->a:Ljava/lang/String;
  000e: invoke-virtual {v0, v1}, Ljava/lang/Object;->equals(Ljava/lang/Object;)Z
  0011: move-result v0
  0012: if-nez v0, :0015
  0014: goto :002a
  0015: iget-object v0, v2, Ltz;->b:Ljava/lang/String;
  0017: iget-object v1, v3, Ltz;->b:Ljava/lang/String;
  0019: invoke-static {v0, v1}, Lg60;->x(Ljava/lang/Object;Ljava/lang/Object;)Z
  001c: move-result v0
  001d: if-nez v0, :0020
  001f: goto :002a
  0020: iget-object v2, v2, Ltz;->c:Ljava/lang/String;
  0022: iget-object v3, v3, Ltz;->c:Ljava/lang/String;
  0024: invoke-virtual {v2, v3}, Ljava/lang/Object;->equals(Ljava/lang/Object;)Z
  0027: move-result v2
  0028: if-nez v2, :002c
  002a: const/4 v2, 0
  002b: return v2
  002c: const/4 v2, 1
  002d: return v2
.end method

.method virtual hashCode()I  #  acc=0x11
  .registers 3 ins=1
  0000: iget-object v0, v2, Ltz;->a:Ljava/lang/String;
  0002: invoke-virtual {v0}, Ljava/lang/String;->hashCode()I
  0005: move-result v0
  0006: mul-int/lit8 v0, v0, 31
  0008: iget-object v1, v2, Ltz;->b:Ljava/lang/String;
  000a: invoke-virtual {v1}, Ljava/lang/String;->hashCode()I
  000d: move-result v1
  000e: add-int/2addr v1, v0
  000f: mul-int/lit8 v1, v1, 31
  0011: iget-object v2, v2, Ltz;->c:Ljava/lang/String;
  0013: invoke-virtual {v2}, Ljava/lang/String;->hashCode()I
  0016: move-result v2
  0017: add-int/2addr v2, v1
  0018: return v2
.end method

.method virtual toString()Ljava/lang/String;  #  acc=0x11
  .registers 3 ins=1
  0000: new-instance v0, Ljava/lang/StringBuilder;
  0002: const-string v1, "FoodItem(name="
  0004: invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V
  0007: iget-object v1, v2, Ltz;->a:Ljava/lang/String;
  0009: invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
  000c: const-string v1, ", info="
  000e: invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
  0011: iget-object v1, v2, Ltz;->b:Ljava/lang/String;
  0013: invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
  0016: const-string v1, ", category="
  0018: invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
  001b: iget-object v2, v2, Ltz;->c:Ljava/lang/String;
  001d: invoke-virtual {v0, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
  0020: const-string v2, ")"
  0022: invoke-virtual {v0, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
  0025: invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;
  0028: move-result-object v2
  0029: return-object v2
.end method
