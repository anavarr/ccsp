process0[
  End
]

|

process1[
  process3!myVar@!"";
  End
]

|

process2[
  process4?myVar@?"";
  If myCondition Then
    process4+"left"@+"";
    process4?myVar@?"";
    End
  Else
    process4+"right"@+"";
    process4?myVar@?"";
    End
]

|

process3[
  process1?myVar@?"";
  process4?myVar@?"";
  If myCondition Then
    process4+"left"@+"";
    process4?myVar@?"";
    End
Else
    process4+"right"@+"";
    End
]

|

process4[
  process2!myVar@!"";
  End
]
