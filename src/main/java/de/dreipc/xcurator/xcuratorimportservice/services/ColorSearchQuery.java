package de.dreipc.xcurator.xcuratorimportservice.services;


import de.dreipc.xcurator.xcuratorimportservice.utils.ColorUtil;

public class ColorSearchQuery {


    private final static String QUERY = """
            {
             "from": <FROM>,
             "size": <SIZE>,
             "collapse": {
                    "field": "artefactId"
             },
             "_source": [ "id", "artefactId"],
              "query": {
                "function_score": {
                  "query": {
                    "bool": {
                      "must": [
                        {
                          "range": {
                            "palette.hue": {
                              "gte": <HUE_LOW>,
                              "lte": <HUE_HIGH>
                            }
                          }
                        },
                                {
                          "range": {
                            "palette.saturation": {
                              "gte": <SATURATION_LOW>,
                              "lte": <SATURATION_HIGH>
                            }
                          }
                        },
                                {
                          "range": {
                            "palette.lightness": {
                              "gte": <LIGHTNESS_LOW>,
                              "lte": <LIGHTNESS_HIGH>
                            }
                          }
                        }
                      ]
                    }
                  },
                  "score_mode": "sum",
                  "functions": [
                    {
                      "exp": {
                        "palette.hue": {
                          "origin": <HUE>,
                          "offset": 1,
                          "scale": "2"
                        }
                      }
                    },
                    {
                      "exp": {
                        "palette.saturation": {
                          "origin": <SATURATION>,
                          "offset": 2,
                          "scale": 4
                        }
                      }
                    },
                    {
                      "exp": {
                        "palette.lightness": {
                          "origin": <LIGHTNESS>,
                          "offset": 2,
                          "scale": 4
                        }
                      }
                    },
                    {
                      "field_value_factor": {
                        "field": "palette.ratio"
                      }
                    }
                  ]
                }
              }
            }
            """;

    public static String query(String hex, int first, int skip) {
        var hsl = ColorUtil.hex2HSL(hex);
        var delta = 0.1;

        return QUERY
                .replace("<HUE>", hsl[0] + "")
                .replace("<HUE_LOW>", (int) (hsl[0] - hsl[0] * delta) + "")
                .replace("<HUE_HIGH>", (int) (hsl[0] + hsl[0] * delta) + "")
                .replace("<SATURATION>", hsl[1] + "")
                .replace("<SATURATION_LOW>", (int) (hsl[1] - hsl[1] * delta) + "")
                .replace("<SATURATION_HIGH>", (int) (hsl[1] + hsl[1] * delta) + "")
                .replace("<LIGHTNESS>", hsl[2] + "")
                .replace("<LIGHTNESS_LOW>", (int) (hsl[2] - hsl[2] * delta) + "")
                .replace("<LIGHTNESS_HIGH>", (int) (hsl[2] + hsl[2] * delta) + "")
                .replace("<FROM>", skip + "")
                .replace("<SIZE>", first + "");
    }
}
