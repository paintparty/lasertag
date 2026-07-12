# Consuming Lasertag as a git dependency

This doc explains, by example, all the possible variations of the coordinate map
you would use to specify Lasertag as a git dependency.

`:git/sha` is always required. 

`:git/tag` is an optional readable label.

`:git/url` is optional. It can be omitted in this case, because the library is named `io.github.paintparty/lasertag`, and the git URL is inferred.

<br>

```clojure
;; deps.edn

{:deps {io.github.paintparty/lasertag {:git/tag "v0.13.2"
                                       :git/sha "aa898c1967d10fc198385f1914893b9c75410d16"}}}
```
<br>

The coordinate map in the example above can take several forms, outlined in the sections below.

<br>

## Tag + SHA (URL inferred, recommended)

The tag documents which release the SHA corresponds to.

```clojure
io.github.paintparty/lasertag 
{:git/tag "v0.13.2" 
 :git/sha "aa898c1967d10fc198385f1914893b9c75410d16"}
```

<br>

## SHA only (URL inferred)

```clojure
io.github.paintparty/lasertag {:git/sha "aa898c1967d10fc198385f1914893b9c75410d16"}
```

<br>

## Explicit URL + SHA

Use this if you want the URL spelled out, or for SSH/private access.

```clojure
io.github.paintparty/lasertag
{:git/url "https://github.com/paintparty/lasertag"
 :git/sha "aa898c1967d10fc198385f1914893b9c75410d16"}
```

<br>

## Explicit URL + tag + SHA

```clojure
io.github.paintparty/lasertag
{:git/url "https://github.com/paintparty/lasertag"
 :git/tag "v0.13.2"
 :git/sha "aa898c1967d10fc198385f1914893b9c75410d16"}
```

<br>

## Resolving the SHA from a tag

If you'd rather not copy the SHA by hand, write the dependency with just
the tag:

```clojure
io.github.paintparty/lasertag {:git/tag "v0.13.2"}
```

then run:

```bash
clojure -X:deps git-resolve-tags
```

This scans your `deps.edn` for git deps that have a `:git/tag` but no
`:git/sha`, resolves each tag to its commit SHA, and writes the SHA back
into the file in place. It needs a `:git/tag` to resolve from. It will not help
the SHA-only form.
