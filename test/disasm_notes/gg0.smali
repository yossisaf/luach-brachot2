.class Lgg0;
.super Ly00;
.source r8-map-id-376676f4a107047edd484746d01b22a49a6869c08ffa71e3048f26960de7e16e

.method direct <init>(Landroid/content/SharedPreferences;Lrj0;)V  #  acc=0x10001
  .registers 10 ins=3
  0000: const/4 v0, 1
  0001: iput v0, v7, Lgg0;->l:I
  0003: iput-object v8, v7, Lgg0;->m:Landroid/content/SharedPreferences;
  0005: iput-object v9, v7, Lgg0;->n:Ljava/lang/Object;
  0007: const-string v5, "MainAppScreen$updateRandomSetting(Landroid/content/SharedPreferences;Landroidx/compose/runtime/MutableState;Z)V"
  0009: const/4 v6, 0
  000a: const/4 v2, 1
  000b: const-class v3, Lf60;
  000d: const-string v4, "updateRandomSetting"
  000f: move-object v1, v7
  0010: invoke-direct/range {v1 .. v6}, Ly00;-><init>(ILjava/lang/Class;Ljava/lang/String;Ljava/lang/String;I)V
  0013: return-void
.end method

.method direct <init>(Lz41;Landroid/content/SharedPreferences;)V  #  acc=0x10001
  .registers 10 ins=3
  0000: const/4 v0, 0
  0001: iput v0, v7, Lgg0;->l:I
  0003: iput-object v8, v7, Lgg0;->n:Ljava/lang/Object;
  0005: iput-object v9, v7, Lgg0;->m:Landroid/content/SharedPreferences;
  0007: const-string v5, "MainAppScreen$deleteFromHistory(Landroidx/compose/runtime/snapshots/SnapshotStateList;Landroid/content/SharedPreferences;Lcom/example/foods/FoodItem;)V"
  0009: const/4 v6, 0
  000a: const/4 v2, 1
  000b: const-class v3, Lf60;
  000d: const-string v4, "deleteFromHistory"
  000f: move-object v1, v7
  0010: invoke-direct/range {v1 .. v6}, Ly00;-><init>(ILjava/lang/Class;Ljava/lang/String;Ljava/lang/String;I)V
  0013: return-void
.end method

.method virtual g(Ljava/lang/Object;)Ljava/lang/Object;  #  acc=0x11
  .registers 11 ins=2
  0000: iget v0, v9, Lgg0;->l:I
  0002: sget-object v1, Lee1;->a:Lee1;
  0004: iget-object v2, v9, Lgg0;->n:Ljava/lang/Object;
  0006: iget-object v9, v9, Lgg0;->m:Landroid/content/SharedPreferences;
  0008: packed-switch v0, :0060
  000b: check-cast v10, Ljava/lang/Boolean;
  000d: invoke-virtual {v10}, Ljava/lang/Boolean;->booleanValue()Z
  0010: move-result v0
  0011: check-cast v2, Lrj0;
  0013: invoke-interface {v2, v10}, Lrj0;->setValue(Ljava/lang/Object;)V
  0016: invoke-interface {v9}, Landroid/content/SharedPreferences;->edit()Landroid/content/SharedPreferences$Editor;
  0019: move-result-object v9
  001a: const-string v10, "show_random"
  001c: invoke-interface {v9, v10, v0}, Landroid/content/SharedPreferences$Editor;->putBoolean(Ljava/lang/String;Z)Landroid/content/SharedPreferences$Editor;
  001f: move-result-object v9
  0020: invoke-interface {v9}, Landroid/content/SharedPreferences$Editor;->apply()V
  0023: return-object v1
  0024: check-cast v10, Ltz;
  0026: invoke-virtual {v10}, Ljava/lang/Object;->getClass()Ljava/lang/Class;
  0029: move-object v3, v2
  002a: check-cast v3, Lz41;
  002c: iget-object v0, v10, Ltz;->a:Ljava/lang/String;
  002e: iget-object v10, v10, Ltz;->c:Ljava/lang/String;
  0030: new-instance v2, Ljava/lang/StringBuilder;
  0032: invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V
  0035: invoke-virtual {v2, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
  0038: const-string v0, "|"
  003a: invoke-virtual {v2, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
  003d: invoke-virtual {v2, v10}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
  0040: invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;
  0043: move-result-object v10
  0044: invoke-virtual {v3, v10}, Lz41;->remove(Ljava/lang/Object;)Z
  0047: invoke-interface {v9}, Landroid/content/SharedPreferences;->edit()Landroid/content/SharedPreferences$Editor;
  004a: move-result-object v9
  004b: const/4 v7, 0
  004c: const/16 v8, 62
  004e: const-string v4, "\n"
  0050: const/4 v5, 0
  0051: const/4 v6, 0
  0052: invoke-static/range {v3 .. v8}, Lbi;->c0(Ljava/lang/Iterable;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lj00;I)Ljava/lang/String;
  0055: move-result-object v10
  0056: const-string v0, "opened_history_v2"
  0058: invoke-interface {v9, v0, v10}, Landroid/content/SharedPreferences$Editor;->putString(Ljava/lang/String;Ljava/lang/String;)Landroid/content/SharedPreferences$Editor;
  005b: move-result-object v9
  005c: invoke-interface {v9}, Landroid/content/SharedPreferences$Editor;->apply()V
  005f: return-object v1
  0060: .packed-switch-data (1)
.end method
