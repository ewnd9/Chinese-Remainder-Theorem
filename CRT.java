import java.math.BigInteger;

/*
 * Written By: Gregory Owen
 * Date: 10/10/11
 * Finds a single congruence equivalent to multiple given congruences
 * (assuming that one exists) via the Chinese Remainder Theorem
 */

public class CRT {
  /*
   * performs the Euclidean algorithm on a and b to find a pair of
   * coefficients (stored in the output array) that correspond to x and y in
   * the equation ax + by = gcd(a,b) constraint: a > b
   */
  public static BigInteger[] euclidean(BigInteger a, BigInteger b) {
    if (b.compareTo(a) > 0) {
      // reverse the order of inputs, run through this method, then
      // reverse outputs
      BigInteger[] coeffs = euclidean(b, a);
      BigInteger[] output = { coeffs[1], coeffs[0] };
      return output;
    }

    BigInteger q = a.divide(b);
    // a = q*b + r --> r = a - q*b
    BigInteger r = a.subtract(q.multiply(b));

    // when there is no remainder, we have reached the gcd and are done
    if (r.compareTo(BigInteger.ZERO) == 0) {
      BigInteger[] output = { BigInteger.ZERO, BigInteger.ONE };
      return output;
    }

    // call the next iteration down (b = qr + r_2)
    BigInteger[] next = euclidean(b, r);

    BigInteger[] output = { next[1], next[0].subtract(q.multiply(next[1])) };
    return output;
  }

  // finds the least positive integer equivalent to a mod m
  public static BigInteger leastPosEquiv(BigInteger a, BigInteger m) {
    // a eqivalent to b mod -m <==> a equivalent to b mod m
    if (m.compareTo(BigInteger.ZERO) < 0)
      return leastPosEquiv(a, m.negate());
    // if 0 <= a < m, then a is the least positive integer equivalent to a
    // mod m
    if (a.compareTo(BigInteger.ZERO) >= 0 && a.compareTo(m) < 0)
      return a;

    // for negative a, find the least negative integer equivalent to a mod m
    // then add m
    if (a.compareTo(BigInteger.ZERO) < 0)
      return leastPosEquiv(a.negate(), m).negate().add(m);

    // the only case left is that of a,m > 0 and a >= m

    // take the remainder according to the Division algorithm
    BigInteger q = a.divide(m);

    /*
     * a = qm + r, with 0 <= r < m r = a - qm is equivalent to a mod m and
     * is the least such non-negative number (since r < m)
     */
    return a.subtract(q.multiply(m));
  }

  public static void main(String[] args) {
    /*
     * the current setup finds a number x such that: x = 2 mod 5, x = 3 mod
     * 7, x = 4 mod 9, and x = 5 mod 11 note that the values in mods must be
     * mutually prime
     */
    BigInteger[] constraints = {
        new BigInteger(
            "2383481670621884097780760129264914572120015660306424632150125456732676861838558575528938709861873384979360106909316726728836243379378130517134103442179232"),
        new BigInteger(
            "3173558442977625800965376976048862571813563198132919552735198379791224619932525792065135784829406439897399030656270441682735411712538727904114016708553509"),
        new BigInteger(
            "4040273319375053306359183228543033411485026326504529516643656990521694250121302582501967606822724745264075967227306907244985415487360198466190779772859808") }; // put
                                                                                              // modular
                                                                                              // contraints
                                                                                              // here
    BigInteger[] mods = {
        new BigInteger(
            "8735671703196820547493672551669849201531676166471241548474354969627979190811593258793332421960407416166599430958569213885352631330183245926781489304105131"),
        new BigInteger(
            "5394743527382642683045764192098771425786851569970656350324897476633433442409656021989413547598408239987270829141870318988663515762168741773549591008930233"),
        new BigInteger(
            "5045599535556013351876181405704178828141027875142491114428188252864005876667044279745862681891448244774925956842135844591555459782296652632892809197810711") }; // put
                                                                                              // moduli
                                                                                              // here

    // M is the product of the mods
    BigInteger M = BigInteger.ONE;
    for (int i = 0; i < mods.length; i++)
      M = M.multiply(mods[i]);

    BigInteger[] multInv = new BigInteger[constraints.length];

    /*
     * this loop applies the Euclidean algorithm to each pair of M/mods[i]
     * and mods[i] since it is assumed that the various mods[i] are pairwise
     * coprime, the end result of applying the Euclidean algorithm will be
     * gcd(M/mods[i], mods[i]) = 1 = a(M/mods[i]) + b(mods[i]), or that
     * a(M/mods[i]) is equivalent to 1 mod (mods[i]). This a is then the
     * multiplicative inverse of (M/mods[i]) mod mods[i], which is what we
     * are looking to multiply by our constraint constraints[i] as per the
     * Chinese Remainder Theorem euclidean(M/mods[i], mods[i])[0] returns
     * the coefficient a in the equation a(M/mods[i]) + b(mods[i]) = 1
     */
    for (int i = 0; i < multInv.length; i++)
      multInv[i] = euclidean(M.divide(mods[i]), mods[i])[0];

    BigInteger x = BigInteger.ZERO;

    // x = the sum over all given i of
    // (M/mods[i])(constraints[i])(multInv[i])
    for (int i = 0; i < mods.length; i++)
      x = x.add((M.divide(mods[i])).multiply(constraints[i]).multiply(
          multInv[i]));

    x = leastPosEquiv(x, M);

    System.out.println("x is equivalent to " + x + " mod " + M);
  }
}
