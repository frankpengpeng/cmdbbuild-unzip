/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.encode.test;

import static com.google.common.io.ByteStreams.toByteArray;
import java.io.IOException;
import java.util.Arrays;
import static org.cmdbuild.utils.encode.CmPackUtils.isPacked;
import static org.cmdbuild.utils.encode.CmPackUtils.packBytes;
import static org.cmdbuild.utils.encode.CmPackUtils.packString;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackBytes;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackBytesIfPacked;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackBytesIfPackedOrBase64;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackString;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackStringIfPacked;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackStringIfPackedOrBase64;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class PackUtilsTest {

    @Test
    public void testPackUtils1() {
        String value = packString("");
        assertEquals("packe3ifcn1m9n30h", value);
        assertTrue(isPacked(value));
        assertEquals("", unpackString(value));
    }

    @Test
    public void testPackUtils2() {
        String value = "fnwvow5v 45vg 45hg9oe %^#@$%^!& +)_+:}:>][;/[;\n\n\rHELLO!",
                packed = packString(value);
        assertEquals("packmd90jix757odes63ju5fyi800k68l9nhzyias5cpc8jbq41glzjqbitkfvvmb87o9011kpdix595kscbp6dqqg1vx5m60j6qf7", packed);
        assertTrue(isPacked(packed));
        assertEquals(value, unpackString(packed));
        assertEquals(value, unpackStringIfPacked(packed));
        assertEquals(value, unpackStringIfPackedOrBase64(packed));
    }

    @Test
    public void testPackUtils3() {
        String value = "hi                                                     enable                                                                                          deflate                     plz",
                packed = packString(value);
        assertEquals("pack5it5xucos67lnyeaepsxue4cgg6lb0eemtk68e2jg25e5mfblumjex832zjygkak09", packed);
        assertTrue(isPacked(packed));
        assertEquals(value, unpackString(packed));
        assertEquals(value, unpackStringIfPacked(packed));
        assertEquals(value, unpackStringIfPackedOrBase64(packed));
    }

    @Test
    public void testPackUtils4() throws IOException {
        byte[] data = toByteArray(getClass().getResourceAsStream("/org/cmdbuild/utils/encode/test/data.raw"));
        String packed = packBytes(data);
        assertEquals("pack2ojg99ijzrnvi3r34aqmkgoya73zmppi5b7ywvs4cv2gm8pemuslywca9fqf9mzt9slcyrrp8qza0qwntqe2cky6wi7yk867bbarufoisifyf6j2rm9vxy3psgyj1sfp90audfk7cjmticbd4i71vylkjrc28wqzoj30abru8sgakk776a9o95hrsr9urb4d8n4jmwe7sikxyatrslxbs4o9kr2jby90v605sk5eqha28xmhpn8p5t9g6wwn9j2gij4klyfhtn1cpf5ejlpyfek4tknyyxomk0wmnx6s9f3f5r9gbflm2ixrc1x5bh8o28lm8smjfx7oybjinfns3d97lu62sx4whor5rf8ba50igluebia8eyvk6orxbjvk32bt1l3x8rj3o3q4j09wdd79233z77k356c3roc5dydi04szs0o0jkwrbpqx1ap15w8c6xltzqh77jkyyejsq66p91a08rasmt3ix2ympm05oxnab103l7sq9vzqbdhzm0szuwl89gxks2i15nplr9w424l07qmyp6te66vdmr2auoclznegpwkc450n8658nwtu37mn09lao9p03681x1zrtsa5a47vi049jaqjhtn9nvaiyl2ysahsmag4uyu8z04hl6mgqrt4gxfmynj025t4x0xv4bi6urx46iamzwun7qky9c1yv5w0kvsebhymv25yvyju2r2mxejp9epyfa3s6qf9zgsbjugffkttmbizrhnbgga53yzv0czyd85iydinb5xgv7o3bojgrrg46qfxsj6bmeq35ch1si6xvacfx6zani01kb9v2lar6icfp6y2n13k2msh9ws3zv4u0j6z0ss31tddcfognjmce3z5ne6p9cfz3pd4fvmhjlu5bo9lwumposvr7jl28hfqsoav8oen9rm0tu9g10ofh86gmakdnpanc5szzfac34jkqwvl8vaj2s3xzf8fal4qity5stbhcxhpyc4x2xj5i98mkw717tsfgdhqqvyb12gbmw5e5v63oyezgibu5q5v5kq758xi08aimrtdndre5ygg3op4jdvi7jr9xyt75m4ckikspyt3db6sbd1zf2ln6e4vvuqk81yunlpeiy306hcw3rffe7kgotzk3uv4bsnwwr1bcxkkqwwzq265r93ri1dfnapv4mm6eyr0f5fpgeucx4q1d20r7xxziveyrbp4yuhzmfm6n5syzbiht9zrzues4sliusp5b3f1pwsgcn61wyhvqk0lh6flzmwkfqgxbq27bxxpejpdxpizxxgkto3mugq4cyyebcerp2vmxs7cmsm626dr4kz6ojt5pnc176utfyan6v6k0d488onjxubseire8sp9bb62gcfb28w2y3q6tf6qzepa1ai8j9d97ghmaix4ufl7nup594ibaq1ok5mk5buvckkhczihr7vfcazo76m7sctg4osp2adlo7p85j9qbmd8l4scxi1sehnknupka3jognmu24w08sqkafl8ckza7pmp1ahe5q7nq0woo482yvrwlzxj6232uj2ee70jt8fwe72ac72xmrhrwuvagj9zglwjkkeo07", packed);
        assertTrue(isPacked(packed));
        assertTrue(Arrays.equals(data, unpackBytes(packed)));
        assertTrue(Arrays.equals(data, unpackBytesIfPacked(packed)));
        assertTrue(Arrays.equals(data, unpackBytesIfPackedOrBase64(packed)));
    }

    @Test
    public void testPackUtils5() throws IOException {
        assertEquals("", unpackStringIfPackedOrBase64(null));
        assertEquals("", unpackStringIfPackedOrBase64(""));
        assertEquals("   ", unpackStringIfPackedOrBase64("   "));
        assertEquals("ciao come va?", unpackStringIfPackedOrBase64("packl4bv3aq3mehgjijg0mrs6mjmmur6hcozj"));
        assertEquals("ciao come va?", unpackStringIfPackedOrBase64("ciao come va?"));
        assertEquals("ciao", unpackStringIfPackedOrBase64("Y2lhbw=="));
        assertEquals("", unpackStringIfPacked(null));
        assertEquals("", unpackStringIfPacked(""));
        assertEquals("   ", unpackStringIfPacked("   "));
        assertEquals("ciao come va?", unpackStringIfPacked("packl4bv3aq3mehgjijg0mrs6mjmmur6hcozj"));
        assertEquals("ciao come va?", unpackStringIfPacked("ciao come va?"));
        assertEquals("Y2lhbw==", unpackStringIfPacked("Y2lhbw=="));
    }

}
