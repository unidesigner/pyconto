package gr.forth.ics.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A set of {@link Interval intervals}. Currently it only supports adding intervals,
 * but not deleting. 
 *
 * @author Andreou Dimitris, email: jim.andreou (at) gmail (dot) com
 */
public class IntervalSet {
    private final LinearInterval head = LinearInterval.newSentinel();

    /**
     * Adds the specified integer to this set.
     *
     * @param integer the integer to add to this set
     */
    public void add(int integer) {
        head.add(integer, integer);
    }

    /**
     * Adds the specified interval to this set.
     *
     * @param startInclusive the start of the interval to add to this set, inclusively
     * @param endInclusive the end of the interval to add to this set, inclusively
     */
    public void add(int startInclusive, int endInclusive) {
        head.add(startInclusive, endInclusive);
    }

    /**
     * Adds the specified interval to this set.
     *
     * @param interval the interval to add to this set
     */
    public void add(Interval interval) {
        head.add(interval.start(), interval.end());
    }

    /**
     * Adds the specified interval set to this set.
     *
     * @param intervalSet the interval set to add to this set
     */
    public void add(IntervalSet intervalSet) {
        head.merge(intervalSet.head);
    }

    /**
     * Returns the number of distinct integers contained in this set.
     *
     * @return the number of distinct integers contained in this set
     */
    public int size() {
        return head.size();
    }

    /**
     * Returns whether the specified integer is contained in this set.
     *
     * @param integer an integer
     * @return whether the specified integer is contained in this set
     */
    public boolean contains(int integer) {
        return head.contains(integer);
    }

    /**
     * Returns all contained integers of this set.
     *
     * @return all contained integers of this set
     */
    public Iterable<Integer> elements() {
        return new Iterable<Integer>() {
            public Iterator<Integer> iterator() {
                if (head.next == null) {
                    return Collections.<Integer>emptyList().iterator();
                }
                return new Iterator<Integer>() {
                    LinearInterval current = head.next; //ignoring sentinel
                    int index = current.start;
                    boolean hasNext = true;

                    public boolean hasNext() {
                        return hasNext;
                    }

                    public Integer next() {
                        if (!hasNext) {
                            throw new NoSuchElementException();
                        }
                        final int next = index;
                        index++;
                        if (index > current.end) {
                            current = current.next;
                            if (current == null) {
                                hasNext = false;
                            } else {
                                index = current.start;
                            }
                        }
                        return next;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                };
            }
        };
    }

    /**
     * Returns the maximal disjoint intervals contained in this set.
     *
     * @return the maximal disjoint intervals contained in this set
     */
    public Iterable<Interval> intervals() {
        return new Iterable<Interval>() {
            public Iterator<Interval> iterator() {
                return new Iterator<Interval>() {
                    LinearInterval current = head;

                    public boolean hasNext() {
                        return current.next != null;
                    }

                    public Interval next() {
                        LinearInterval next = current.next;
                        current = next;
                        return next;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    @Override
    public String toString() {
        return head.toString();
    }

    static class LinearInterval implements Interval {
        int start;
        int end;
        LinearInterval next;

        LinearInterval(int start, int end, LinearInterval next) {
            this.start = start;
            this.end = end;
            this.next = next;
        }

        public int start() {
            return start;
        }

        public int end() {
            return end;
        }

        static LinearInterval newSentinel() {
            return new LinearInterval(-1, Integer.MIN_VALUE, null) {

                @Override
                LinearInterval add(int s, int e) {
                    if (next != null) {
                        if (e < next.start - 1) {
                            next = new LinearInterval(s, e, next);
                        } else if (s < next.start) {
                            next.start = s;
                        }
                        return next.add(s, e);
                    } else {
                        return next = new LinearInterval(s, e, null);
                    }
                }

                @Override
                public String toString() {
                    if (next == null) {
                        return "[]";
                    } else {
                        return next.toString();
                    }
                }
            };
        }

        public boolean contains(int index) {
            LinearInterval current = this;
            while (true) {
                if (index < current.start) {
                    return false;
                } else if (index <= current.end) {
                    return true;
                } else if (current.next != null) {
                    current = current.next;
                } else {
                    return false;
                }
            }
        }

        //returns the interval that now represents the segment [s, e]
        LinearInterval add(int s, int e) {
            //assert s <= e;
            //assert !(s < start && e > end)
            LinearInterval current = this;
            while (true) {
                //s >= current.start
                if (e <= current.end) {
                    //s >= current.start;
                    //e <= current.end
                    return current;
                } else if (s <= current.end + 1) {
                    //current.start <= s <= current.end + 1
                    //e > current.end
                    current.end = e;

                    //current.start <= s <= current.end
                    //e == current.end
                    //subsume next?
                    LinearInterval next = current.next;
                    while (next != null) {
                        //next == current.next
                        if (next.start <= e + 1) {
                            current.end = Math.max(e, next.end);
                            current.next = next.next;
                        } else {
                            break;
                        }
                        next = current.next;
                    }
                    return current;
                }

                //current.end + 1 < s
                //e > current.end
                LinearInterval next = current.next;
                if (next != null) {
                    if (e < next.start - 1) {
                        //current.end + 1 < s
                        //current.end < e < next.start - 1
                        return current.next = new LinearInterval(s, e, next);
                    } else if (s >= next.start) {
                        //s >= next.start
                        //e >= next.start - 1
                        current = next;
                        continue;
                    } else {
                        //current.end + 1 < s < next.start
                        //e >= next.start - 1
                        next.start = s;

                        //s == next.start
                        //e >= s
                        current = next;

                        //s == current.start
                        //e >= s
                        continue;
                    }
                } else {
                    current.next = new LinearInterval(s, e, null);
                }
            }
        }

        void merge(LinearInterval interval) {
            LinearInterval next = interval.next; //ignoring sentinel
            LinearInterval base = this;
            while (next != null) {
                base = base.add(next.start, next.end);
                next = next.next;
            }
        }

        int size() {
            int size = 0;
            LinearInterval current = this.next; //ignoring sentinel
            while (current != null) {
                size += current.end - current.start + 1;
                current = current.next;
            }
            return size;
        }

        public boolean isEmpty() {
            return start > end;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[").append(start).append("-").append(end);
            LinearInterval next = this.next;
            while (next != null) {
                sb.append(",").append(next.start).append("-").append(next.end);
                next = next.next;
            }
            sb.append("]");
            return sb.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof LinearInterval)) {
                return false;
            }
            LinearInterval other = (LinearInterval)o;
            return start == other.start && end == other.end;
        }

        @Override
        public int hashCode() {
            return start + end * 39;
        }
    }
}