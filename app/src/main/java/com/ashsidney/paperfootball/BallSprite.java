package com.ashsidney.paperfootball;

import android.content.res.Resources;


public class BallSprite extends AnimSprite
{
  public BallSprite (float animTime, Resources res)
  {
    super(R.drawable.ball, (float)(2.56 / Math.PI), res);
    int[] horizAnim = { R.drawable.ball_h00, R.drawable.ball_h01, R.drawable.ball_h02, R.drawable.ball_h03,
        R.drawable.ball_h04, R.drawable.ball_h05, R.drawable.ball_h06, R.drawable.ball_h07,
        R.drawable.ball_h08, R.drawable.ball_h09, R.drawable.ball_h10, R.drawable.ball_h11,
        R.drawable.ball_h12, R.drawable.ball_h13, R.drawable.ball_h14, R.drawable.ball_h15,
        R.drawable.ball_h16 };
    addAnim(horizAnim, res, animTime);
    int[] vertAnim = { R.drawable.ball_v00, R.drawable.ball_v01, R.drawable.ball_v02, R.drawable.ball_v03,
        R.drawable.ball_v04, R.drawable.ball_v05, R.drawable.ball_v06, R.drawable.ball_v07,
        R.drawable.ball_v08, R.drawable.ball_v09, R.drawable.ball_v10, R.drawable.ball_v11,
        R.drawable.ball_v12, R.drawable.ball_v13, R.drawable.ball_v14, R.drawable.ball_v15,
        R.drawable.ball_v16 };
    addAnim(vertAnim, res, animTime);
  }
}
