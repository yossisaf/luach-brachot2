.class Lhk;
.super Ljava/lang/Object;

.method direct <init>(Landroid/content/SharedPreferences;Lyp0;I)V
  .registers 4 ins=4
  0000: iput v3, v0, Lhk;->d:I
  0002: iput-object v1, v0, Lhk;->e:Landroid/content/SharedPreferences;
  0004: iput-object v2, v0, Lhk;->f:Lyp0;
  0006: invoke-direct {v0}, Ljava/lang/Object;-><init>()V
  0009: return-void
.end method

.method virtual g(Ljava/lang/Object;)Ljava/lang/Object;
  .registers 5 ins=2
  0000: iget v0, v3, Lhk;->d:I
  0002: sget-object v1, Lee1;->a:Lee1;
  0004: iget-object v2, v3, Lhk;->f:Lyp0;
  0006: iget-object v3, v3, Lhk;->e:Landroid/content/SharedPreferences;
  0008: check-cast v4, Ljava/lang/Integer;
  000a: invoke-virtual {v4}, Ljava/lang/Integer;->intValue()I
  000d: move-result v4
  000e: packed-switch v0, :0044
  0011: invoke-virtual {v2, v4}, Lyp0;->h(I)V
  0014: invoke-interface {v3}, Landroid/content/SharedPreferences;->edit()Landroid/content/SharedPreferences$Editor;
  0017: move-result-object v3
  0018: const-string v0, "theme_mode"
  001a: invoke-interface {v3, v0, v4}, Landroid/content/SharedPreferences$Editor;->putInt(Ljava/lang/String;I)Landroid/content/SharedPreferences$Editor;
  001d: move-result-object v3
  001e: invoke-interface {v3}, Landroid/content/SharedPreferences$Editor;->apply()V
  0021: return-object v1
  0022: invoke-virtual {v2, v4}, Lyp0;->h(I)V
  0025: invoke-interface {v3}, Landroid/content/SharedPreferences;->edit()Landroid/content/SharedPreferences$Editor;
  0028: move-result-object v3
  0029: const-string v0, "text_size_option"
  002b: invoke-interface {v3, v0, v4}, Landroid/content/SharedPreferences$Editor;->putInt(Ljava/lang/String;I)Landroid/content/SharedPreferences$Editor;
  002e: move-result-object v3
  002f: invoke-interface {v3}, Landroid/content/SharedPreferences$Editor;->apply()V
  0032: return-object v1
  0033: invoke-virtual {v2, v4}, Lyp0;->h(I)V
  0036: invoke-interface {v3}, Landroid/content/SharedPreferences;->edit()Landroid/content/SharedPreferences$Editor;
  0039: move-result-object v3
  003a: const-string v0, "color_option"
  003c: invoke-interface {v3, v0, v4}, Landroid/content/SharedPreferences$Editor;->putInt(Ljava/lang/String;I)Landroid/content/SharedPreferences$Editor;
  003f: move-result-object v3
  0040: invoke-interface {v3}, Landroid/content/SharedPreferences$Editor;->apply()V
  0043: return-object v1
  0044: .packed-switch-data (2)
.end method
