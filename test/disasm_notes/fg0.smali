.class Lfg0;
.super Ly00;
.source r8-map-id-376676f4a107047edd484746d01b22a49a6869c08ffa71e3048f26960de7e16e

.method direct <init>(Lc51;Landroid/content/SharedPreferences;)V  #  acc=0x10001
  .registers 10 ins=3
  0000: const/4 v0, 1
  0001: iput v0, v7, Lfg0;->l:I
  0003: iput-object v8, v7, Lfg0;->n:Ljava/lang/Object;
  0005: iput-object v9, v7, Lfg0;->m:Landroid/content/SharedPreferences;
  0007: const-string v5, "MainAppScreen$clearAllStats(Landroidx/compose/runtime/snapshots/SnapshotStateSet;Landroid/content/SharedPreferences;)V"
  0009: const/4 v6, 0
  000a: const/4 v2, 0
  000b: const-class v3, Lf60;
  000d: const-string v4, "clearAllStats"
  000f: move-object v1, v7
  0010: invoke-direct/range {v1 .. v6}, Ly00;-><init>(ILjava/lang/Class;Ljava/lang/String;Ljava/lang/String;I)V
  0013: return-void
.end method

.method direct <init>(Lz41;Landroid/content/SharedPreferences;)V  #  acc=0x10001
  .registers 10 ins=3
  0000: const/4 v0, 0
  0001: iput v0, v7, Lfg0;->l:I
  0003: iput-object v8, v7, Lfg0;->n:Ljava/lang/Object;
  0005: iput-object v9, v7, Lfg0;->m:Landroid/content/SharedPreferences;
  0007: const-string v5, "MainAppScreen$clearAllHistory(Landroidx/compose/runtime/snapshots/SnapshotStateList;Landroid/content/SharedPreferences;)V"
  0009: const/4 v6, 0
  000a: const/4 v2, 0
  000b: const-class v3, Lf60;
  000d: const-string v4, "clearAllHistory"
  000f: move-object v1, v7
  0010: invoke-direct/range {v1 .. v6}, Ly00;-><init>(ILjava/lang/Class;Ljava/lang/String;Ljava/lang/String;I)V
  0013: return-void
.end method

.method virtual a()Ljava/lang/Object;  #  acc=0x11
  .registers 6 ins=1
  0000: iget v0, v5, Lfg0;->l:I
  0002: sget-object v1, Lee1;->a:Lee1;
  0004: iget-object v2, v5, Lfg0;->m:Landroid/content/SharedPreferences;
  0006: iget-object v5, v5, Lfg0;->n:Ljava/lang/Object;
  0008: packed-switch v0, :005a
  000b: check-cast v5, Lc51;
  000d: invoke-virtual {v5}, Lc51;->clear()V
  0010: invoke-interface {v2}, Landroid/content/SharedPreferences;->getAll()Ljava/util/Map;
  0013: move-result-object v5
  0014: invoke-interface {v2}, Landroid/content/SharedPreferences;->edit()Landroid/content/SharedPreferences$Editor;
  0017: move-result-object v0
  0018: invoke-interface {v5}, Ljava/util/Map;->keySet()Ljava/util/Set;
  001b: move-result-object v5
  001c: check-cast v5, Ljava/lang/Iterable;
  001e: invoke-interface {v5}, Ljava/lang/Iterable;->iterator()Ljava/util/Iterator;
  0021: move-result-object v5
  0022: invoke-interface {v5}, Ljava/util/Iterator;->hasNext()Z
  0025: move-result v2
  0026: if-eqz v2, :003e
  0028: invoke-interface {v5}, Ljava/util/Iterator;->next()Ljava/lang/Object;
  002b: move-result-object v2
  002c: check-cast v2, Ljava/lang/String;
  002e: invoke-virtual {v2}, Ljava/lang/Object;->getClass()Ljava/lang/Class;
  0031: const-string v3, "open_count|"
  0033: const/4 v4, 0
  0034: invoke-static {v2, v3, v4}, Lr61;->E(Ljava/lang/String;Ljava/lang/String;Z)Z
  0037: move-result v3
  0038: if-eqz v3, :0022
  003a: invoke-interface {v0, v2}, Landroid/content/SharedPreferences$Editor;->remove(Ljava/lang/String;)Landroid/content/SharedPreferences$Editor;
  003d: goto :0022
  003e: const-string v5, "unique_opened_items"
  0040: invoke-interface {v0, v5}, Landroid/content/SharedPreferences$Editor;->remove(Ljava/lang/String;)Landroid/content/SharedPreferences$Editor;
  0043: invoke-interface {v0}, Landroid/content/SharedPreferences$Editor;->apply()V
  0046: return-object v1
  0047: check-cast v5, Lz41;
  0049: invoke-virtual {v5}, Lz41;->clear()V
  004c: invoke-interface {v2}, Landroid/content/SharedPreferences;->edit()Landroid/content/SharedPreferences$Editor;
  004f: move-result-object v5
  0050: const-string v0, "opened_history_v2"
  0052: invoke-interface {v5, v0}, Landroid/content/SharedPreferences$Editor;->remove(Ljava/lang/String;)Landroid/content/SharedPreferences$Editor;
  0055: move-result-object v5
  0056: invoke-interface {v5}, Landroid/content/SharedPreferences$Editor;->apply()V
  0059: return-object v1
  005a: .packed-switch-data (1)
.end method
