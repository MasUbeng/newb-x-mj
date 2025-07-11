$input v_texcoord0, mj_pl, mj_pos

#include <bgfx_shader.sh>

#ifndef INSTANCING
  #include <newb/config.h>
  #include <newb/functions/tonemap.h>

  uniform vec4 SunMoonColor;
  
  //SAMPLER2D_AUTOREG(s_SunMoonTexture);
#endif

  uniform vec4 FogAndDistanceControl;
  uniform vec4 FogColor;
  uniform vec4 ViewPositionAndTime;
  SAMPLER2D(s_SunMoonTexture, 0);

float noise(float t)
{
	return fract(cos(t) * 3800.);
}

vec3 lensflare(vec2 u,vec2 pos)
{
	vec2 main = u-pos;
	vec2 uvd = u*(length(u));
	highp float intensity = 1.5;
	
	float ang = atan(main.y, main.x);
	float dist=length(u); //main
  dist = pow(dist,.01);
	float n = noise(0.);
	
	float f0 = (1.0/(length(u-12.)*16.0+1.0)) * 2.;
	
	f0 = f0*(sin((n*2.0)*12.0)*.1+dist*.1+.8);

	float f2 = max(1.0/(1.0+32.0*pow(length(uvd+0.8*pos),2.0)),.0)*00.25;
	float f22 = max(1.0/(1.0+32.0*pow(length(uvd+0.85*pos),2.0)),.0)*00.23;
	float f23 = max(1.0/(1.0+32.0*pow(length(uvd+0.9*pos),2.0)),.0)*00.21;
	
	vec2 uvx = mix(u,uvd,-0.5);
	
	float f4 = max(0.02-pow(length(uvx+0.45*pos),2.4),.0)*6.0;
	float f42 = max(0.02-pow(length(uvx+0.5*pos),2.4),.0)*5.0;
	float f43 = max(0.02-pow(length(uvx+0.55*pos),2.4),.0)*3.0;
	
	uvx = mix(u,uvd,-.4);
	
	float f5 = max(0.02-pow(length(uvx+0.3*pos),5.5),.0)*2.0;
	float f52 = max(0.02-pow(length(uvx+0.5*pos),5.5),.0)*2.0;
	float f53 = max(0.01-pow(length(uvx+0.7*pos),5.5),.0)*2.0;
	
	uvx = mix(u,uvd,-0.5);
	
	float f6 = max(0.02-pow(length(uvx+0.1*pos),1.6),.0)*6.0;
	float f62 = max(0.01-pow(length(uvx+0.125*pos),1.6),.0)*3.0;
	float f63 = max(0.01-pow(length(uvx+0.15*pos),1.6),.0)*5.0;
	
	highp vec3 c = vec3(0.0);
	c.r+=f2+f4+f5+f6; 
  c.g+=f22+f42+f52+f62; 
  c.b+=f23+f43+f53+f63;
	c+=vec3(f0);
	
	return c * intensity;
}

vec3 cc(vec3 color, float factor,float factor2) // color modifier
{
	float w = color.x+color.y+color.z;
	return mix(color,vec3(w)*factor,w*factor2);
}


void main() {
  /*#ifndef INSTANCING
    vec4 color = texture2D(s_SunMoonTexture, v_texcoord0)*5.;
    color.rgb *= SunMoonColor.rgb;
    color.rgb *= 4.4*color.rgb;
    float tr = 1.0 - SunMoonColor.a;
    color.a *= 1.0 - (1.0-NL_SUNMOON_RAIN_VISIBILITY)*tr*tr;
    color.rgb = colorCorrection(color.rgb);
    gl_FragColor = color;
  #else
    gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);
  #endif*/
  
    vec4 diffuse = texture2D(s_SunMoonTexture, v_texcoord0);

    /*vec2 u = -mj_pl.xz*.1;
	vec3 lf = mj_pos;

    vec3 c = vec3(1.6,1.3,1.0)*lensflare(lf.xz*13.0, u)*2.;
    c = cc(c,.5,.1) * .5;
    diffuse.rgb += c + .2 -clamp( length(-mj_pos) / FogAndDistanceControl.z * 25., 0., .2 );*/
    
    float lp = length(-mj_pos);
    vec4 fc = FogColor;
    float hujan = (1.0-pow(FogAndDistanceControl.y,11.0));
    float sore = pow(max(min(1.0-fc.b*1.2,1.0),0.0),0.5);

    float area = (.1 -clamp(lp, .0, .1)) * 10.;
    diffuse = mix( vec4( .0 ), diffuse, area);

  fc.rgb = max(fc.rgb, .4);
  fc.a *= 1. -hujan;
  diffuse.a = min(fc.a, .5);
  area = .2 -clamp( length(-mj_pos) / FogAndDistanceControl.z * 200., 0., .2 );
  vec3 af = mix( vec3( 1. ), vec3( 1., .5, 0. ), sore ) * area;
  vec2 u = -mj_pl.xz * .1;
  vec3 c = vec3( 1.4, 1.2, 1. ) * lensflare( mj_pos.xz * 13., u ) * 2.;
  c = cc(c,.5,.1) * .5;
  diffuse.rgb += c + af;
  diffuse.a += length(c) * fc.a;
    gl_FragColor = clamp(diffuse*0.7, 0., 1.);
  
}
