package me.matthewedevelopment.atheriallib.utilities;

public  class XZ {
        private int x;
        private int z;

        public XZ(int x, int z) {
            this.x = x;
            this.z = z;
        }

        @Override
        public int hashCode() {
            return x+ z;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj==null)return false;
            if (obj instanceof XZ) {
                return x==((XZ) obj).x&& z == ((XZ) obj).z;
            }
            return false;
        }
    }