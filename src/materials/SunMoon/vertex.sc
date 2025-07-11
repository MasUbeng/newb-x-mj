$input a_position, a_texcoord0
$output v_texcoord0, mj_pl, mj_pos

#include <bgfx_shader.sh>

#ifndef INSTANCING
  #include <newb/config.h>
#endif

uniform vec4 FogAndDistanceControl;
uniform vec4 FogColor;
uniform vec4 ViewPositionAndTime;

void main() {
  v_texcoord0 = a_texcoord0;
  mj_pos = a_position;
  mj_pl = vec4(a_position, 1.0) * u_modelViewProj;
  /*#ifndef INSTANCING
    vec3 pos = a_position;

    pos.xz *= NL_SUNMOON_SIZE;
    #ifdef NL_SUNMOON_ANGLE
      float angle = NL_SUNMOON_ANGLE*0.0174533;
      float sinA = sin(angle);
      float cosA = cos(angle);
      pos.xz = vec2(pos.x*cosA - pos.z*sinA, pos.x*sinA + pos.z*cosA);
    #endif
    gl_Position = mul(u_modelViewProj, vec4(a_position, 1.0));
  #else
    gl_Position = vec4(0.0, 0.0, 0.0, 0.0);
  #endif*/
  
   float malam = pow(max(min(1.0-FogColor.r*1.5,1.0),0.0),1.2);
    vec4 size = vec4(18., 1., 18., 1.);
    size.xz /= mix( 1., 8., malam );
    gl_Position = mul(u_modelViewProj, vec4(a_position, 1.0));
    gl_Position = u_modelViewProj * (vec4(a_position, 1.0) * size);
    //gl_Position = WORLDVIEWPROJ * (POSITION * size);
}
