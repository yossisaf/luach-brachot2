.class Lgf0;
.super Ljava/lang/Object;

.method direct <init>(Ljava/lang/String;Ld51;Lz41;Landroid/content/SharedPreferences;)V
  .registers 5 ins=5
  0000: invoke-direct {v0}, Ljava/lang/Object;-><init>()V
  0003: iput-object v1, v0, Lgf0;->d:Ljava/lang/String;
  0005: iput-object v2, v0, Lgf0;->e:Ld51;
  0007: iput-object v3, v0, Lgf0;->f:Lz41;
  0009: iput-object v4, v0, Lgf0;->g:Landroid/content/SharedPreferences;
  000b: return-void
.end method

.method virtual g(Ljava/lang/Object;)Ljava/lang/Object;
  .registers 10 ins=2
  0000: check-cast v9, Lv70;
  0002: invoke-virtual {v9}, Ljava/lang/Object;->getClass()Ljava/lang/Class;
  0005: iget-object v9, v8, Lgf0;->d:Ljava/lang/String;
  0007: invoke-static {v9}, Lr61;->G(Ljava/lang/CharSequence;)Ljava/lang/CharSequence;
  000a: move-result-object v9
  000b: invoke-virtual {v9}, Ljava/lang/Object;->toString()Ljava/lang/String;
  000e: move-result-object v9
  000f: invoke-static {v9}, Lr61;->A(Ljava/lang/CharSequence;)Z
  0012: move-result v0
  0013: if-eqz v0, :0016
  0015: goto :004c
  0016: iget-object v0, v8, Lgf0;->f:Lz41;
  0018: invoke-virtual {v0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;
  001b: new-instance v1, Ljava/util/ArrayList;
  001d: invoke-direct {v1, v0}, Ljava/util/ArrayList;-><init>(Ljava/util/Collection;)V
  0020: invoke-virtual {v1, v9}, Ljava/util/ArrayList;->remove(Ljava/lang/Object;)Z
  0023: const/4 v2, 0
  0024: invoke-virtual {v1, v2, v9}, Ljava/util/ArrayList;->add(ILjava/lang/Object;)V
  0027: const/4 v9, 5
  0028: invoke-static {v9, v1}, Lbi;->i0(ILjava/util/List;)Ljava/util/List;
  002b: move-result-object v2
  002c: invoke-virtual {v0}, Lz41;->clear()V
  002f: invoke-virtual {v0, v2}, Lz41;->addAll(Ljava/util/Collection;)Z
  0032: iget-object v9, v8, Lgf0;->g:Landroid/content/SharedPreferences;
  0034: invoke-interface {v9}, Landroid/content/SharedPreferences;->edit()Landroid/content/SharedPreferences$Editor;
  0037: move-result-object v9
  0038: const/4 v6, 0
  0039: const/16 v7, 62
  003b: const-string v3, "|"
  003d: const/4 v4, 0
  003e: const/4 v5, 0
  003f: invoke-static/range {v2 .. v7}, Lbi;->c0(Ljava/lang/Iterable;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lj00;I)Ljava/lang/String;
  0042: move-result-object v0
  0043: const-string v1, "search_history"
  0045: invoke-interface {v9, v1, v0}, Landroid/content/SharedPreferences$Editor;->putString(Ljava/lang/String;Ljava/lang/String;)Landroid/content/SharedPreferences$Editor;
  0048: move-result-object v9
  0049: invoke-interface {v9}, Landroid/content/SharedPreferences$Editor;->apply()V
  004c: iget-object v8, v8, Lgf0;->e:Ld51;
  004e: if-eqz v8, :0055
  0050: check-cast v8, Lsq;
  0052: invoke-virtual {v8}, Lsq;->a()V
  0055: sget-object v8, Lee1;->a:Lee1;
  0057: return-object v8
.end method
