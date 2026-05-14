# Consumer R8 rule bundled into the AAR.
#
# LogTagGenerator filters its own frames by class-name prefix to derive the caller's tag.
# Without -keepnames, R8 obfuscates these classes and every release log gets tagged with
# the logger's own (now-renamed) internals instead.
-keepnames class com.asamm.kmp.logger.**
